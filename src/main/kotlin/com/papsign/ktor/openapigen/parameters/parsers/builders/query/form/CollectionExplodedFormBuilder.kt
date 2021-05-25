package com.papsign.ktor.openapigen.parameters.parsers.builders.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderParameters
import com.papsign.ktor.openapigen.parameters.parsers.builders.withMatchingKey
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType

abstract class CollectionExplodedFormBuilder(type: KType): FormBuilder() {


    override val explode: Boolean = true

    abstract fun transform(lst: List<Any?>): Any?

    private val converter: Converter = ConverterFactory.buildConverterForced(ListToArray.arrayComponentKType(type))

    override fun build(key: String, parameters: BuilderParameters): Any? {
        return (parameters.withMatchingKey(key) ?: listOf()).map(converter::convert).let(::transform)
    }
}
