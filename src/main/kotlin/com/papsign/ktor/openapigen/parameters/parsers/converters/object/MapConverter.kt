package com.papsign.ktor.openapigen.parameters.parsers.converters.`object`

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class MapConverter(type: KType): Converter {

    val keyConverter = PrimitiveConverterFactory.buildConverterForced(type.arguments[0].type!!)
    val valueConverter = PrimitiveConverterFactory.buildConverterForced(type.arguments[1].type!!)

    override fun convert(value: String): Any? {
        return value.split(",").windowed(2).associate { keyConverter.convert(it[0]) to valueConverter.convert(it[1]) }
    }

    companion object: ConverterSelector {

        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.isSubclassOf(Map::class)
        }

        override fun create(type: KType): MapConverter {
            return MapConverter(type)
        }
    }
}
