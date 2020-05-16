package com.papsign.ktor.openapigen.parameters.parsers.converters.`object`

import com.papsign.ktor.openapigen.annotations.mapping.openAPIName
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class ObjectConverter(type: KType): MappedConverter {

    private val builderMap: Map<KParameter, Converter>
    private val constructor: KFunction<Any>

    init {
        val kclass = type.jvmErasure
        if (kclass.isData) {
            constructor = kclass.primaryConstructor ?: error("Parameter objects must have primary constructors")
            builderMap = constructor.parameters.associateWith { parameter -> PrimitiveConverterFactory.buildConverter(parameter.type) ?: error("Invalid type ${parameter.type} in object $type, only enums and primitives are allowed") }
        } else {
            error("Only data classes are currently supported for Parameter objects")
        }
    }

    override fun convert(value: String): Any? {
        return convert(value.split(",").windowed(2).associate { it[0] to it[1] })
    }

    override fun convert(map: Map<String, String>): Any? {
        return try { constructor.callBy(builderMap.mapValues { (key, value) -> map[key.openAPIName]?.let { value.convert(it) }  }) } catch (e: InvocationTargetException) { null }
    }

    companion object: ConverterSelector {

        override fun canHandle(type: KType): Boolean {
            return true
        }

        override fun create(type: KType): ObjectConverter {
            return ObjectConverter(type)
        }
    }
}
