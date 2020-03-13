package com.papsign.ktor.openapigen.model.server

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.server.ServerVariableModel

data class ServerModel(
    var url: String,
    var description: String? = null,
    var variables: MutableMap<String, ServerVariableModel> = mutableMapOf()
): DataModel
