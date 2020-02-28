package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.openapi.Parameter
import com.papsign.ktor.openapigen.parameters.handlers.ParameterHandler
import io.ktor.http.Parameters

object UnitParameterHandler :
    ParameterHandler<Unit> {
    override fun parse(parameters: Parameters) = Unit
    override fun getParameters(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<Parameter<*>> {
        return listOf()
    }
}
