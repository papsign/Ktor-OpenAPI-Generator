package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.openapi.ExternalDocumentation
import com.papsign.ktor.openapigen.openapi.Tag

interface APITag {
    val name: String
    val description: String
    val externalDocs: ExternalDocumentation?
            get() = null

    fun toTag(): Tag {
        return Tag(name, description, externalDocs)
    }
}