package com.papsign.ktor.openapigen.model.operation

import com.papsign.ktor.openapigen.model.DataModel

data class RequestBodyModel(
    var content: MutableMap<String, MediaTypeModel<*>>,
    var description: String? = null,
    var required: Boolean? = null
): DataModel
