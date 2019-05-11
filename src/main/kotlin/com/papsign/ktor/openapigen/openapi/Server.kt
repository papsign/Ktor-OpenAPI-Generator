package com.papsign.ktor.openapigen.openapi

data class Server(
    var url: String,
    var description: String? = null,
    var variables: MutableMap<String, ServerVariable>? = null
)