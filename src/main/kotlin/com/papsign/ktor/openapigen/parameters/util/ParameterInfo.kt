package com.papsign.ktor.openapigen.parameters.util

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

data class ParameterInfo(
    val key: String,
    val pathAnnotation: PathParam? = null,
    val queryAnnotation: QueryParam? = null
) {
    constructor(key: String, parameter: KParameter) : this(
        key,
        parameter.findAnnotation<PathParam>(),
        parameter.findAnnotation<QueryParam>()
    )
}
