package co.uk.mvt.banana.configuration

class Logger : co.uk.mvt.banana.interfaces.Log {
    private var defaultTag: String? = null

    override fun d(tag: String, message: String) {
        print(message)
    }

    override fun i(tag: String, message: String) {
        print(message)
    }

    override fun w(tag: String, message: String) {
        print(message)
    }

    override fun v(tag: String, message: String) {
        print(message)
    }

    override fun e(tag: String, message: String) {
        print(message)
    }

    override fun setTag(tag: String?) {
        defaultTag = tag
    }

    override fun d(message: String) {
        print(message)
    }

    override fun i(message: String) {
        print(message)
    }

    override fun w(message: String) {
        print(message)
    }

    override fun v(message: String) {
        print(message)
    }

    override fun e(message: String) {
        print(message)
    }

    override fun e(e: Throwable) {
        print(e.message)
    }

    override fun clone(): Any {
        val result = Logger()
        result.setTag(defaultTag)
        return result
    }
}
