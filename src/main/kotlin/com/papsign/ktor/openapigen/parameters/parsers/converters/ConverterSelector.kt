package com.papsign.ktor.openapigen.parameters.parsers.converters

import kotlin.reflect.KType

interface ConverterSelector {
    fun canHandle(type: KType): Boolean
    fun create(type: KType): Converter
}
