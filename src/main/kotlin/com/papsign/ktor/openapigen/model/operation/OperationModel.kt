package com.papsign.ktor.openapigen.model.operation

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.info.ExternalDocumentationModel
import com.papsign.ktor.openapigen.model.security.SecurityModel
import com.papsign.ktor.openapigen.model.server.ServerModel

data class OperationModel(
    var tags: List<String>? = null,
    var summary: String? = null,
    var description: String? = null,
    var externalDocs: ExternalDocumentationModel? = null,
    var operationId: String? = null,
    var parameters: List<ParameterModel<*>>? = null,
    var requestBody: RequestBodyModel? = null,
    var responses: MutableMap<String, StatusResponseModel> = mutableMapOf(),
    // var callbacks ...
    var deprecated: Boolean? = null,
    var security: List<SecurityModel>? = null,
    var servers: List<ServerModel>? = null
): DataModel
