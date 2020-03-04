package com.papsign.ktor.openapigen.parameters.parsers.builders.path.label

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import kotlin.reflect.KType

class LabelBuilder(type: KType, override val explode: Boolean): Builder<PathParamStyle> {
    override val style: PathParamStyle = PathParamStyle.label

    private val converter: Converter = ConverterFactory.buildConverterForced(type)

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        val value = parameters[key]?.let { it[0] }?.removePrefix(".") ?: return null
        val adjusted = if (explode) value.replace('=', ',').replace('.', ',') else value
        return converter.convert(adjusted)
    }

    companion object: BuilderSelector<LabelBuilder> {
        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return true
        }

        override fun create(type: KType, explode: Boolean): LabelBuilder {
            return LabelBuilder(type, explode)
        }
    }
}
