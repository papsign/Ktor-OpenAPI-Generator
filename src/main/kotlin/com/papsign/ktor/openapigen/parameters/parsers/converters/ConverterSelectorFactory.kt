package com.papsign.ktor.openapigen.parameters.parsers.converters

import kotlin.reflect.KType

open class ConverterSelectorFactory(vararg val selectors: ConverterSelector): ConverterFactory {
    override fun buildConverter(type: KType): Converter? {
        return selectors.find { it.canHandle(type) }?.create(type)
    }
}
