package com.papsign.ktor.openapigen.parameters.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.ParameterModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import io.ktor.http.Parameters

object UnitParameterHandler :
    ParameterHandler<Unit> {
    override fun parse(parameters: Parameters) = Unit
    override fun getParameters(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<ParameterModel<*>> {
        return listOf()
    }
}
