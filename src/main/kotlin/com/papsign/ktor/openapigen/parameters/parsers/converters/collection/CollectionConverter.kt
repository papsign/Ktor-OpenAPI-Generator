package com.papsign.ktor.openapigen.parameters.parsers.converters.collection

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType

abstract class CollectionConverter(type: KType): Converter {

    private val converter: Converter = PrimitiveConverterFactory.buildConverterForced(ListToArray.arrayComponentKType(type))

    abstract fun transform(lst: List<Any?>): Any?

    override fun convert(value: String): Any? {
        return value.split(",").map(converter::convert).let(::transform)
    }
}
