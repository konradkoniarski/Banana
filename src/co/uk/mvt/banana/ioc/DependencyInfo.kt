package co.uk.mvt.banana.ioc

class DependencyInfo(var contract: Class<*>, var implementation: Class<*>) {
    var isBuildImmediately = true
}
