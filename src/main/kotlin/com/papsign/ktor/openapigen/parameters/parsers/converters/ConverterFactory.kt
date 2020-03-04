package com.papsign.ktor.openapigen.parameters.parsers.converters

import com.papsign.ktor.openapigen.parameters.parsers.converters.`object`.MapConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.`object`.ObjectConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.collection.ArrayConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.collection.ListConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.EnumConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverter
import kotlin.reflect.KType

interface ConverterFactory {
    fun buildConverter(type: KType): Converter?
    fun buildConverterForced(type: KType): Converter = buildConverter(type) ?: error("No ${this.javaClass.declaringClass?.simpleName ?: this.javaClass.simpleName} Converter exists for type $type")

    companion object: ConverterSelectorFactory(
        PrimitiveConverter,
        EnumConverter,
        ListConverter,
        ArrayConverter,
        MapConverter,
        ObjectConverter
    )
}
