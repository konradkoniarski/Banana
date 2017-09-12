package co.uk.mvt.banana.ioc

import co.uk.mvt.banana.interfaces.Log

import java.lang.reflect.InvocationTargetException
import java.util.ArrayList
import java.util.HashMap

object Container {

    private val cache = HashMap<Class<*>, Any>()
    private val mappings = HashMap<Class<*>, Class<*>>()
    private val buildImmediately = HashMap<Class<*>, Boolean>()
    private var log: Log? = null

    private var initialized = false

    init {
        Container.cache.clear()
        Container.mappings.clear()
        Container.initialized = false
    }

    @Throws(ClassNotFoundException::class)
    fun setup(containerSetup: ContainerSetup) {
        for (dependencyInfo in containerSetup.definition) {
            val contract = dependencyInfo.contract
            Container.buildImmediately.put(contract!!, dependencyInfo.isBuildImmediately)
            Container[contract] = dependencyInfo.implementation
        }
    }

    fun setLog(log: Log) {
        Container.log = log
    }

    @Throws(ClassNotFoundException::class)
    fun init() {
        Container.initialized = true
        for (clz in Container.mappings.keys) {
            if (Container.buildImmediately[clz] == null || Container.buildImmediately[clz] === false) {
                continue
            }
            if (Container.cache.containsKey(clz)) {
                continue
            }
            Container.cache.put(clz, Container.getObject(clz, ArrayList<String>()))
        }
        Container.log = Container.getInstance(Log::class.java)
        if (Container.log == null) {
            throw IllegalStateException("No class implements " + Log::class.java.name + " found. Please add default logger.\n" + Container.dump())
        }
        Container.dump()
    }

    @Throws(ClassNotFoundException::class)
    operator fun set(contract: Class<*>, className: String) {
        if (Container.mappings.containsKey(contract)) {
            throw IllegalStateException(String.format("Definition for class %s already exists", contract.name))
        }
        Container.mappings.put(contract, Class.forName(className))
    }

    @Throws(ClassNotFoundException::class)
    operator fun set(contract: Class<*>, clz: Class<*>) {
        Container.mappings.put(contract, clz)
    }

    operator fun set(contract: Class<*>, `object`: Any) {
        Container.mappings.put(contract, `object`.javaClass)
        Container.cache.put(contract, `object`)
    }

    @Throws(ClassNotFoundException::class)
    fun <T> getInstance(name: String): T? {
        if (!Container.initialized) {
            throw IllegalStateException("Container not initialized")
        }
        return Container.cache[Class.forName(name)] as T
    }

    fun <T> getInstance(clz: Class<T>): T? {
        if (!Container.initialized) {
            throw IllegalStateException("Container not initialized")
        }
        return Container.cache[clz] as T
    }

    private fun getRecursivePath(recursiveInvocationPath: List<String>): String {
        val sb = StringBuilder()
        val separator = "->"
        for (name in recursiveInvocationPath) {
            sb.append(name).append(separator)
        }
        return sb.substring(0, sb.length - separator.length)
    }

    @Throws(ClassNotFoundException::class)
    private fun getObject(clz: Class<*>, recursiveInvocationPath: MutableList<String>): Any {
        Container.log(String.format("Building object of %s", clz.name))

        Container.checkCircularDependency(clz, recursiveInvocationPath)
        recursiveInvocationPath.add(clz.name)

        val obj = Container.getInstance(clz)
        if (obj != null) {
            return obj
        }
        val objectImp = Container.mappings[clz] ?: throw ClassNotFoundException()
        Container.log(String.format("Apply mapping %s => %s", clz.name, objectImp.name))
        val constructors = objectImp.constructors

        for (constructor in constructors) {
            try {
                Container.log(String.format("Evaluate constructor %s", constructor.name))
                if (constructor.parameterTypes.size == 0) {
                    // parameterless
                    Container.log(String.format("No parameters needed building..."))
                    return constructor.newInstance()
                } else {
                    // get params recursively
                    val paramInstances = ArrayList<Any>()
                    Container.log(String.format("%d parameters needed", constructor.parameterTypes.size))
                    for (constructorParamClz in constructor.parameterTypes) {
                        Container.log(String.format("Looking for parameter %s", constructorParamClz.name))
                        val paramInstance = Container.getObject(constructorParamClz, ArrayList(recursiveInvocationPath))
                        paramInstances.add(paramInstance)
                        Container.log(String.format("Parameter of type %s found %s", constructorParamClz.name, paramInstance))
                    }
                    Container.log(String.format("All parameters ready. Building %s ", clz.name))
                    return constructor.newInstance(*paramInstances.toTypedArray())
                }
            } catch (e: IllegalArgumentException) {
                Container.log(e)
            } catch (e: InstantiationException) {
                Container.log(e)
            } catch (e: IllegalAccessException) {
                Container.log(e)
            } catch (e: InvocationTargetException) {
                Container.log(e)
            }

        }
        throw ClassNotFoundException(String.format("Can't find class %s for %s", clz.name, Container.getRecursivePath(recursiveInvocationPath)))
    }

    private fun checkCircularDependency(clz: Class<*>, recursiveInvocationPath: List<String>) {
        if (recursiveInvocationPath.contains(clz.name)) {
            throw IllegalStateException("Circular dependencies " + Container.getRecursivePath(recursiveInvocationPath))
        }
    }

    fun clearCache() {
        Container.cache.clear()
    }

    fun clear() {
        Container.cache.clear()
        Container.mappings.clear()
        Container.initialized = false
    }

    @Throws(ClassNotFoundException::class)
    fun getClassFor(clz: Class<*>): Class<*> {
        if (Container.mappings.containsKey(clz)) {
            return Container.mappings[clz] as Class<*>
        }
        throw ClassNotFoundException()
    }

    @Throws(ClassNotFoundException::class)
    fun getClassName(className: String): String {
        return Container.getClassFor(Class.forName(className)).name
    }

    @Throws(ClassNotFoundException::class)
    fun getClassName(clz: Class<*>): String {
        return Container.getClassFor(clz).name
    }

    private fun log(message: String) {
        if (Container.log != null) {
            Container.log!!.d(message)
        }
    }

    private fun log(exception: Exception) {
        if (Container.log != null) {
            Container.log!!.e(exception)
        }
    }

    private fun dump(): String {
        val sb = StringBuilder()
        sb.append("CACHE:\n")
        for (key in Container.cache.keys) {
            sb.append("Contract: ").append(key.name).append(" -> ").append(
                    if (Container.cache[key] == null)
                        "null"
                    else Container.cache[key]!!.javaClass.getName())
                    .append("\n")
        }
        sb.append("MAPPINGS:\n")
        for (key in Container.mappings.keys) {
            sb.append("Contract: ").append(key.name).append(" -> ").append(if (Container.mappings[key] == null) "null" else Container.mappings[key]!!.getName())
                    .append("\n")
        }
        return sb.toString()
    }
}
