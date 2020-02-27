package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.util.genPathParseFunc
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo
import io.ktor.http.Parameters

class EnumParameterParser(info: ParameterInfo, val enumMap: Map<String, *>, val nullable: Boolean) : InfoParameterParser(info, { style ->
        when (style) {
            QueryParamStyle.DEFAULT, QueryParamStyle.form-> QueryParamStyle.form
            else -> {
                log.warn("Using non-form style for enum type, it is undefined in the OpenAPI standard, reverting to form style")
                QueryParamStyle.form
            }
        }
    }) {

    private fun parse(parameter: String?): Any? {
        return parameter?.let { enumMap[it] }
    }

    private val parseFunc = pathStyle?.let { genPathParseFunc(key, it, ::parse) } ?: ::parse

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }

    companion object {
        val log = classLogger<EnumParameterParser>()
    }
}
