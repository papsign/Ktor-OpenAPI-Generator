package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import io.ktor.http.Parameters

interface ParameterParser {
    val key: String
    val reservedKeys: Set<String>
        get() = setOf(key)

    val queryStyle: QueryParamStyle?
    val pathStyle: PathParamStyle?

    val explode: Boolean

    fun parse(parameters: Parameters): Any?
}
