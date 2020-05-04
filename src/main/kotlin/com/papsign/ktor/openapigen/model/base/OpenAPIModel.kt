package com.papsign.ktor.openapigen.model.base

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.info.ExternalDocumentationModel
import com.papsign.ktor.openapigen.model.info.InfoModel
import com.papsign.ktor.openapigen.model.info.TagModel
import com.papsign.ktor.openapigen.model.security.SecurityModel
import com.papsign.ktor.openapigen.model.server.ServerModel

data class OpenAPIModel(
    var info: InfoModel = InfoModel(),
    var openapi: String = "3.0.0",
    var servers: MutableList<ServerModel> = mutableListOf(),
    var paths: MutableMap<String, PathItemModel> = mutableMapOf(),
    var components: ComponentsModel = ComponentsModel(),
    var security: LinkedHashSet<SecurityModel> = LinkedHashSet(),
    var tags: LinkedHashSet<TagModel> = LinkedHashSet(),
    var externalDocs: ExternalDocumentationModel? = null
): DataModel

