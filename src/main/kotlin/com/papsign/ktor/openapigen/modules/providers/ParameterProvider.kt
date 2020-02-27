package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.parameters.ParamBuilder
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.openapi.Parameter

interface ParameterProvider: OpenAPIModule {
    fun  getParameters(builder: ParamBuilder): List<Parameter<*>>
}
