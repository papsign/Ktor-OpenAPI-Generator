package com.papsign.ktor.openapigen.model.operation

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.schema.SchemaModel

data class HeaderModel<T>(
    var required: Boolean,
    var description: String? = null,
    var deprecated: Boolean? = null,
    var allowEmptyValue: Boolean? = null,
    var schema: SchemaModel<T>? = null,
    var example: T? = null,
    var examples: MutableMap<String, T>? = null
    // incomplete
): DataModel
