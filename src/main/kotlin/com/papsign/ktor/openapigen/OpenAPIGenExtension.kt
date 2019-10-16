package com.papsign.ktor.openapigen

interface OpenAPIGenExtension {
    fun onInit(gen: OpenAPIGen)
}
