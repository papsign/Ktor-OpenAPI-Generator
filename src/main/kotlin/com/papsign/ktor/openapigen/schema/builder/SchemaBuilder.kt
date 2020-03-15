package com.papsign.ktor.openapigen.schema.builder

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

interface SchemaBuilder {
    /**
     * the supertype to be matched to the type in order to select this builder
     */
    val superType: KType

    /**
     * @throws error when type is unexpected
     */
    fun build(type: KType, builder: SchemaBuilder): SchemaModel<*>

    fun checkType(type: KType) {
        if (!type.isSubtypeOf(superType)) error("${this::class} cannot build type $type, only subtypes of $superType are supported")
    }
}

