package com.papsign.ktor.openapigen.modules.openapi

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule

interface HandlerModule: OpenAPIModule {
    fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>)
}
