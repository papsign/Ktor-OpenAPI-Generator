package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class OptionalDeepBuilder(type: KType) : DeepBuilder {
    private val converter: Converter = ConverterFactory.buildConverterForced(type.arguments[0].type!!)

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        return parameters[key]?.let { it[0] }?.let { value ->
            when (value) {
                "" -> Optional.empty()
                "null" -> Optional.empty()
                else -> Optional.ofNullable(converter.convert(value))
            }
        }
    }


    companion object : BuilderSelector<OptionalDeepBuilder> {
        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return type.jvmErasure.isSubclassOf(Optional::class)
        }

        override fun create(type: KType, explode: Boolean): OptionalDeepBuilder {
            return OptionalDeepBuilder(type)
        }
    }
}