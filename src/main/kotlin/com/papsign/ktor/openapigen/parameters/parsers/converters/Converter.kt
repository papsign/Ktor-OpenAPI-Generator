package com.papsign.ktor.openapigen.parameters.parsers.converters

interface Converter {
    fun convert(value: String): Any?
}
