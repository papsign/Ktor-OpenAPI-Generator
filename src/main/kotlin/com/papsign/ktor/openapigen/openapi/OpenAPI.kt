package com.papsign.ktor.openapigen.openapi

data class OpenAPI(
    var info: Info = Info(),
    var openapi: String = "3.0.0",
    var servers: MutableList<Server> = mutableListOf(),
    var paths: MutableMap<String, PathItem> = mutableMapOf(),
    var components: Components = Components(),
    var security: LinkedHashSet<Security> = LinkedHashSet(),
    var tags: LinkedHashSet<Tag> = LinkedHashSet(),
    var externalDocs: ExternalDocumentation? = null
) {
    data class Contact(
        var name: String? = null,
        var url: String? = null,
        var email: String? = null
    )

    data class Info(
        var title: String = "Default",
        var version: String = "0.0.1",
        var description: String? = null,
        var termsOfService: String? = null,
        var contact: Contact? = null,
        var license: License? = null
    )

    data class License(
        var name: String = "All Rights Reserved",
        var url: String? = null
    )
}