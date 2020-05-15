package com.papsign.ktor.openapigen.model.operation

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.info.ExampleModel
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.parameters.ParameterStyle

data class ParameterModel<T>(
    var name: String,
    var `in`: ParameterLocation,
    var required: Boolean = true,
    var description: String? = null,
    var deprecated: Boolean? = null,
    var allowEmptyValue: Boolean? = null,
    var schema: SchemaModel<T>? = null,
    var example: T? = null,
    var examples: MutableMap<String, ExampleModel<T>>? = null,
    var style: ParameterStyle<*>? = null,
    var explode: Boolean = false
    // incomplete
): DataModel
