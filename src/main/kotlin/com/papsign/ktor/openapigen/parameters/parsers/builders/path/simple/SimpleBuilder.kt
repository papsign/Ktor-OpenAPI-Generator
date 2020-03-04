package com.papsign.ktor.openapigen.parameters.parsers.builders.path.simple

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import kotlin.reflect.KType

class SimpleBuilder(val type: KType, override val explode: Boolean): Builder<PathParamStyle> {
    override val style: PathParamStyle = PathParamStyle.simple
    private val converter: Converter = ConverterFactory.buildConverterForced(type)

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        val value = parameters[key]?.let { it[0] } ?: return null
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
