package com.papsign.ktor.openapigen.model.info

import com.papsign.ktor.openapigen.model.DataModel

data class ExternalDocumentationModel(
    var url: String,
    var description: String? = null
): DataModel


