package com.papsign.ktor.openapigen.parameters.parsers.converters.collection

import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelectorFactory
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.EnumConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverter

object CollectionConverterFactory: ConverterSelectorFactory(
    PrimitiveConverter,
    EnumConverter,
    ListConverter,
    ArrayConverter
)
