package com.papsign.ktor.openapigen.parameters.parsers.builders.path.matrix

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderParameters
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.builders.withMatchingKey
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import com.papsign.ktor.openapigen.parameters.parsers.converters.`object`.MappedConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.collection.ListedConverter
import kotlin.reflect.KType

class MatrixBuilder(type: KType, override val explode: Boolean): Builder<PathParamStyle> {
    override val style: PathParamStyle = PathParamStyle.matrix

    private val converter: Converter = ConverterFactory.buildConverterForced(type)

    override fun build(key: String, parameters: BuilderParameters): Any? {
        val value = parameters.withMatchingKey(key)?.let { it[0] } ?: return null
        return if (explode) {
            val groups = value.removePrefix(";").split(';').map { it.split('=').let { Pair(it[0], it.getOrElse(1){""}) } }
            when (converter) {
                is MappedConverter -> converter.convert(groups.toMap())
                is ListedConverter -> converter.convert(groups.map { it.second })
                else -> converter.convert(groups.first().second)
            }
        } else {
            converter.convert(value.removePrefix(";$key="))
        }
    }

    companion object: BuilderSelector<MatrixBuilder> {
        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return true
        }

        override fun create(type: KType, explode: Boolean): MatrixBuilder {
            return MatrixBuilder(type, explode)
        }
    }
}
