package co.uk.mvt.banana.interfaces

interface Log : Cloneable {

    fun setTag(tag: String?)

    fun d(message: String)

    fun i(message: String)

    fun w(message: String)

    fun v(message: String)

    fun e(message: String)

    fun d(tag: String, message: String)

    fun i(tag: String, message: String)

    fun w(tag: String, message: String)

    fun v(tag: String, message: String)

    fun e(tag: String, message: String)

    fun e(e: Throwable)

    public override fun clone(): Any
}
