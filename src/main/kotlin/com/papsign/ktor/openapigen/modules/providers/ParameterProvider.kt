package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.openapi.Parameter

interface ParameterProvider: OpenAPIModule {
    fun  getParameters(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<Parameter<*>>
}
