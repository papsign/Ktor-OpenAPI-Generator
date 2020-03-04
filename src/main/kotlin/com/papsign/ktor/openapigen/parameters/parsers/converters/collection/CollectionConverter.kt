package com.papsign.ktor.openapigen.parameters.parsers.converters.collection

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType

abstract class CollectionConverter(type: KType): ListedConverter {

    private val converter: Converter = PrimitiveConverterFactory.buildConverterForced(ListToArray.arrayComponentKType(type))

    abstract fun transform(lst: List<Any?>): Any?

    override fun convert(value: String): Any? {
        return convert(value.split(","))
    }

    override fun convert(list: List<String>): Any? {
        return list.map(converter::convert).let(::transform)
    }
}
