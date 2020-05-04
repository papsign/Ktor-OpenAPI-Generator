package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.ParameterModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule

interface ParameterProvider: OpenAPIModule {
    fun  getParameters(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<ParameterModel<*>>
}
