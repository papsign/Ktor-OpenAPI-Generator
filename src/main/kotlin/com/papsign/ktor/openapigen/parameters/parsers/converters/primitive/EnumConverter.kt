package com.papsign.ktor.openapigen.parameters.parsers.converters.primitive

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class EnumConverter(type: KType): Converter {

    private val enumMap = type.jvmErasure.java.enumConstants.associateBy { it.toString() }

    override fun convert(value: String): Any? {
        return enumMap[value]
    }

    companion object: ConverterSelector {
        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.java.isEnum
        }

        override fun create(type: KType): EnumConverter {
            return EnumConverter(
                type
            )
        }
    }
}
