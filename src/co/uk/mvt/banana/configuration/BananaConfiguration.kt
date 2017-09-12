package co.uk.mvt.banana.configuration

import co.uk.mvt.banana.ioc.Container
import co.uk.mvt.banana.ioc.ContainerSetup
import co.uk.mvt.banana.ioc.DependencyInfo

import java.util.ArrayList

object BananaConfiguration {

    @Synchronized fun prepareContainer() {
        try {
            Container.setLog(Logger())
            val definitions = ArrayList<DependencyInfo>()
            //definitions.add(new DependencyInfo(contract, implementation))

            val containerSetup = ContainerSetup()
            containerSetup.setDefinitions(definitions)
            Container.setup(containerSetup)
            Container.init()
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }

    }
}
