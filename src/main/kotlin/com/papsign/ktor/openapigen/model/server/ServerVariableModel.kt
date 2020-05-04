package com.papsign.ktor.openapigen.model.server

import com.papsign.ktor.openapigen.model.DataModel

data class ServerVariableModel(
    var default: String,
    var enum: MutableList<String> = mutableListOf(),
    var description: String? = null
): DataModel
