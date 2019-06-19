package com.papsign.ktor.openapigen.openapi

data class Operation(
    var tags: List<String>? = null,
    var summary: String? = null,
    var description: String? = null,
    var externalDocs: ExternalDocumentation? = null,
    var operationId: String? = null,
    var parameters: List<Parameter<*>>? = null,
    var requestBody: RequestBody? = null,
    var responses: MutableMap<String, StatusResponse> = mutableMapOf(),
    // var callbacks ...
    var deprecated: Boolean? = null,
    var security: List<Security>? = null,
    var servers: List<Server>? = null
)