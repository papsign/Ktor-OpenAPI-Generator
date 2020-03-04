package com.papsign.ktor.openapigen.parameters.parsers.builders.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterFactory
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import kotlin.reflect.KType

open class ConverterFormBuilder(type: KType, factory: ConverterFactory): FormBuilder() {

    private val converter: Converter = factory.buildConverterForced(type)
    override val explode: Boolean = false

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        return parameters[key]?.let{it[0]}?.let { converter.convert(it) }
    }

    open class Selector(private val factory: ConverterFactory, private val ignoreExplode: Boolean = false): BuilderSelector<ConverterFormBuilder> {
        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return (ignoreExplode || !explode) && factory.buildConverter(type) != null
        }

        override fun create(type: KType, exploded: Boolean): ConverterFormBuilder {
            return ConverterFormBuilder(type, factory)
        }
    }

    companion object {
        operator fun invoke(factory: ConverterFactory, ignoreExplode: Boolean = false): Selector {
            return Selector(factory, ignoreExplode)
        }

        val primitive = invoke(PrimitiveConverterFactory, true)
        val all = invoke(ConverterFactory, true)
    }
}
