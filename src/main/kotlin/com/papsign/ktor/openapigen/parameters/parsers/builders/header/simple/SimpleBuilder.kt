package com.papsign.ktor.openapigen.parameters.parsers.builders.header.simple

import com.papsign.ktor.openapigen.parameters.HeaderParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderParameters
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.builders.withMatchingKey
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import kotlin.reflect.KType

class SimpleBuilder(val type: KType, override val explode: Boolean): Builder<HeaderParamStyle> {
    override val style: HeaderParamStyle = HeaderParamStyle.simple
    private val converter: Converter = ConverterFactory.buildConverterForced(type)

    override fun build(key: String, parameters: BuilderParameters): Any? {
        val value = parameters.withMatchingKey(key)?.let { it[0] } ?: return null
        val adjusted = if (explode) value.replace('=', ',') else value
        return converter.convert(adjusted)
    }

    companion object: BuilderSelector<SimpleBuilder> {
        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return true
        }

        override fun create(type: KType, explode: Boolean): SimpleBuilder {
            return SimpleBuilder(type, explode)
        }
    }
}
