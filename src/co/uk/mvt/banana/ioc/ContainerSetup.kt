package co.uk.mvt.banana.ioc

import java.util.ArrayList

class ContainerSetup {
    var definition: List<DependencyInfo> = ArrayList()
        private set

    fun setDefinitions(definitions: List<DependencyInfo>) {
        this.definition = definitions
    }

}
