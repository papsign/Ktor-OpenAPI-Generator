package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.modules.openapi.OperationModule

fun info(summary: String? = null, description: String? = null) = EndpointInfo(summary, description)

data class EndpointInfo(
    val summary: String? = null,
    val description: String? = null
) : OperationModule, RouteOpenAPIModule {
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: OperationModel) {
        operation.description = description
        operation.summary = summary
    }
}
