package com.papsign.ktor.openapigen.parameters.parsers.converters.collection

import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter

interface ListedConverter: Converter {
    fun convert(list: List<String>): Any?
}
