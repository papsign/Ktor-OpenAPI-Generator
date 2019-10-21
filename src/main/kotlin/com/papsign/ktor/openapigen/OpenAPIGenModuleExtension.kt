package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.modules.OpenAPIModule

/**
 * implement this to automatically register an object as [OpenAPIModule] in the global context
 * only works if the object is in a package declared in [OpenAPIGen.Configuration.scanPackagesForModules]
 */
interface OpenAPIGenModuleExtension: OpenAPIModule, OpenAPIGenExtension {
    override fun onInit(gen: OpenAPIGen) {
        gen.globalModuleProvider.registerModule(this)
    }
}
