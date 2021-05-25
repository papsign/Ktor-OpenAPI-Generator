package com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderParameters
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilder
import com.papsign.ktor.openapigen.parameters.parsers.builders.withMatchingKey
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType

abstract class CollectionDelimitedBuilder(type: KType, val delimiter: String): FormBuilder() {

    override val explode: Boolean = false

    abstract fun transform(lst: List<Any?>): Any?

    private val converter: Converter = ConverterFactory.buildConverterForced(ListToArray.arrayComponentKType(type))

    override fun build(key: String, parameters: BuilderParameters): Any? {
        return parameters.withMatchingKey(key)?.let { it[0] }?.split(delimiter)?.map { converter.convert(it) }
    }
}
