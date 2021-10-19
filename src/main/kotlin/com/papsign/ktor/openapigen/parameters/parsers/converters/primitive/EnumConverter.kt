package com.papsign.ktor.openapigen.parameters.parsers.converters.primitive

import com.papsign.ktor.openapigen.exceptions.OpenAPIBadContentException
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class EnumConverter(val type: KType) : Converter {

    private val enumMap = type.jvmErasure.java.enumConstants.associateBy { it.toString() }

    override fun convert(value: String): Any? {
        if (enumMap.containsKey(value)) {
            return enumMap[value]
        } else {
            throw OpenAPIBadContentException(
                "Invalid value [$value] for enum parameter of type ${type.jvmErasure.simpleName}. Expected: [${
                    enumMap.values.joinToString(
                        ","
                    )
                }]"
            )
        }
    }

    companion object : ConverterSelector {
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
