package com.papsign.ktor.openapigen.parameters.parsers.converters.`object`

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class OptionalConverter(type: KType) : Converter {
    private val converter: Converter = ConverterFactory.buildConverterForced(type.arguments[0].type!!)

    override fun convert(value: String): Any? {
        return when (value) {
            "" -> Optional.empty()
            "null" -> Optional.empty()
            else -> Optional.ofNullable(converter.convert(value))
        }
    }


    companion object : ConverterSelector {
        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.isSubclassOf(Optional::class)
        }

        override fun create(type: KType): OptionalConverter {
            return OptionalConverter(type)
        }
    }
}