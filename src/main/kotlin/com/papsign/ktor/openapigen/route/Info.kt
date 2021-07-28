package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.modules.openapi.OperationModule

/**
 * Adds a summary, description and deprecation flag for the endpoint being configured
 */
fun info(summary: String? = null, description: String? = null, deprecated: Boolean? = null) = EndpointInfo(summary, description, deprecated)

data class EndpointInfo(
    val summary: String? = null,
    val description: String? = null,
    val deprecated: Boolean? = null
) : OperationModule, RouteOpenAPIModule {
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: OperationModel) {
        operation.description = description
        operation.summary = summary
        operation.deprecated = deprecated
    }
}
