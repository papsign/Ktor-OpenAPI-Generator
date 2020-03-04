package com.papsign.ktor.openapigen.parameters.parsers.converters.`object`

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class MapConverter(type: KType): MappedConverter {

    val keyConverter = PrimitiveConverterFactory.buildConverterForced(type.arguments[0].type!!)
    val valueConverter = PrimitiveConverterFactory.buildConverterForced(type.arguments[1].type!!)

    override fun convert(value: String): Any? {
        return convert(value.split(",").windowed(2).associate { it[0] to it[1] })
    }

    override fun convert(map: Map<String, String>): Any? {
        return map.entries.associate { (key, value) -> keyConverter.convert(key) to valueConverter.convert(value) }
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
