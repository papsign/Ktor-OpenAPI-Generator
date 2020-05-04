package com.papsign.ktor.openapigen.model.operation

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.schema.SchemaModel

data class MediaTypeModel<T>(
    val schema: SchemaModel<T>? = null,
    val example: T? = null,
    val examples: MutableMap<String, T>? = null,
    val encoding: Map<String, MediaTypeEncodingModel>? = null
): DataModel
