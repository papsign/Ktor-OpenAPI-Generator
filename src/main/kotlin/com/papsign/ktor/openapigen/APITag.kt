package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.model.info.ExternalDocumentationModel
import com.papsign.ktor.openapigen.model.info.TagModel


interface APITag {
    val name: String
    val description: String
    val externalDocs: ExternalDocumentationModel?
            get() = null

    fun toTag(): TagModel {
        return TagModel(name, description, externalDocs)
    }
}
