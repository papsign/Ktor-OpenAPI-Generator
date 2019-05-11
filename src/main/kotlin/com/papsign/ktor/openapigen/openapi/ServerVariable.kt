package com.papsign.ktor.openapigen.openapi

data class ServerVariable(
    var default: String,
    var enum: MutableList<String>? = null,
    var description: String? = null
)