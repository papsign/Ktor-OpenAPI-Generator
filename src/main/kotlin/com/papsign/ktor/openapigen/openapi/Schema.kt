package com.papsign.ktor.openapigen.openapi

sealed class Schema<T> {

    data class SchemaObj<T>(
        var properties: Map<String, Schema<*>>,
        var required: List<String>,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.`object`
    ) : Schema<T>()

    data class SchemaMap<T : Map<String, U>, U>(
        var additionalProperties: Schema<U>,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.`object`
    ) : Schema<T>()

    data class SchemaEnum<T>(
        var enum: List<String>,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.string
    ) : Schema<T>()

    data class SchemaArr<T>(
        var items: Schema<*>?,
        var nullable: Boolean = false,
        var example: T? = null,
        var type: DataType = DataType.array
    ) : Schema<T>()

    data class SchemaLitteral<T>(
        var type: DataType,
        var format: DataFormat? = null,
        var nullable: Boolean = false,
        var minimum: T? = null,
        var maximum: T? = null,
        var example: T? = null
    ) : Schema<T>()

    data class SchemaRef<T>(override val `$ref`: String) : Schema<T>(),
        Ref<Schema<T>>

    data class OneSchemaOf<T>(val oneOf: List<Schema<out T>>) : Schema<T>()
}