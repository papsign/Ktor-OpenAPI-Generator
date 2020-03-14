package com.papsign.ktor.openapigen.model.schema

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.base.RefModel

sealed class SchemaModel<T>: DataModel {

    data class SchemaModelObj<T>(
        var properties: Map<String, SchemaModel<*>>,
        var required: List<String>,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.`object`
    ) : SchemaModel<T>()

    data class SchemaModelMap<T : Map<String, U>, U>(
        var additionalProperties: SchemaModel<U>,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.`object`
    ) : SchemaModel<T>()

    data class SchemaModelEnum<T>(
        var enum: List<String>,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.string
    ) : SchemaModel<T>()

    data class SchemaModelArr<T>(
        var items: SchemaModel<*>?,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.array
    ) : SchemaModel<T>()

    data class SchemaModelLitteral<T>(
        var type: DataType,
        var format: DataFormat? = null,
        var nullable: Boolean = false,
        var minimum: T? = null,
        var maximum: T? = null,
        var example: T? = null
    ) : SchemaModel<T>()

    data class SchemaModelRef<T>(override val `$ref`: String) : SchemaModel<T>(), RefModel<SchemaModel<T>>

    data class OneSchemaModelOf<T>(val oneOf: List<SchemaModel<out T>>) : SchemaModel<T>()
}
