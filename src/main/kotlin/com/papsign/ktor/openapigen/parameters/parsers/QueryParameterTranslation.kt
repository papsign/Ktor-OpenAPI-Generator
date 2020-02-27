package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.QueryParamStyle

data class QueryParameterTranslation(val key: String, val style: QueryParamStyle, val explode: Boolean) {
    val translatedName: String? = when (style) {
        QueryParamStyle.form -> "$key${if (explode) "*" else ""}"
        QueryParamStyle.spaceDelimited -> if (explode) "$key*" else null
        QueryParamStyle.pipeDelimited -> if (explode) "$key*" else null
        QueryParamStyle.deepObject -> null
    }
}
