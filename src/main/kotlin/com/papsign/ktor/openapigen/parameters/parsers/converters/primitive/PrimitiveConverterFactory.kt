package com.papsign.ktor.openapigen.parameters.parsers.converters.primitive

import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelectorFactory

object PrimitiveConverterFactory: ConverterSelectorFactory(
    PrimitiveConverter,
    EnumConverter
)
