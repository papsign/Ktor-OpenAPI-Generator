package com.papsign.ktor.openapigen.model.schema

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.base.RefModel

sealed class SchemaModel<T> : DataModel {

    abstract var example: T?
    abstract var examples: List<T>?
    abstract var description: String?

    data class SchemaModelObj<T>(
        var properties: Map<String, SchemaModel<*>>,
        var required: List<String>,
        var nullable: Boolean = false,
        override var example: T? = null,
        override var examples: List<T>? = null,
        var type: DataType = DataType.`object`,
        override var description: String? = null,
        var discriminator: Discriminator<T>? = null
    ) : SchemaModel<T>()

    data class SchemaModelMap<T : Map<String, U>, U>(
        var additionalProperties: SchemaModel<U>,
        var nullable: Boolean = false,
        override var example: T? = null,
        override var examples: List<T>? = null,
        var type: DataType = DataType.`object`,
        override var description: String? = null
    ) : SchemaModel<T>()

    data class SchemaModelEnum<T>(
        var enum: List<String>,
        var nullable: Boolean = false,
        override var example: T? = null,
        override var examples: List<T>? = null,
        var type: DataType = DataType.string,
        override var description: String? = null
    ) : SchemaModel<T>()

    data class SchemaModelArr<T>(
        var items: SchemaModel<*>?,
        var nullable: Boolean = false,
        override var example: T? = null,
        override var examples: List<T>? = null,
        var uniqueItems: Boolean? = null,
        var minItems: Int? = null,
        var maxItems: Int? = null,
        var type: DataType = DataType.array,
        override var description: String? = null
    ) : SchemaModel<T>()

    data class SchemaModelLitteral<T>(
        var type: DataType? = null,
        var format: DataFormat? = null,
        var nullable: Boolean = false,
        var minimum: T? = null,
        var maximum: T? = null,
        var minLength: Int? = null,
        var maxLength: Int? = null,
        var pattern: String? = null,
        override var example: T? = null,
        override var examples: List<T>? = null,
        override var description: String? = null
    ) : SchemaModel<T>()

    data class SchemaModelRef<T>(override val `$ref`: String) : SchemaModel<T>(), RefModel<SchemaModel<T>> {
        override var example: T? = null
        override var examples: List<T>? = null
        override var description: String? = null
    }

    data class OneSchemaModelOf<T>(
        val oneOf: List<SchemaModel<out T>>,
        var properties: Map<String, SchemaModel<*>>? = null,
        val discriminator: Discriminator<T>? = null
    ) :
        SchemaModel<T>() {
        override var example: T? = null
        override var examples: List<T>? = null
        override var description: String? = null
    }
}
