package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo
import com.papsign.ktor.openapigen.parameters.util.genPathParseFunc
import io.ktor.http.Parameters

class PrimitiveParameterParser<T>(
    info: ParameterInfo,
    val parse: (String?) -> T
) : InfoParameterParser(info, { style ->
    when (style) {
        QueryParamStyle.DEFAULT, QueryParamStyle.form-> QueryParamStyle.form to false
        else -> {
            log.warn("Using non-form style for primitive type, it is undefined in the OpenAPI standard, reverting to form style")
            QueryParamStyle.form to false
        }
    }
}) {

    private val parseFunc = pathStyle?.let { genPathParseFunc(key, it, parse) } ?: parse

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }

    companion object {
        val log = classLogger<PrimitiveParameterParser<*>>()
    }
}
