package com.papsign.ktor.openapigen.annotations.parameters

import com.papsign.ktor.openapigen.model.operation.ParameterLocation
import com.papsign.ktor.openapigen.parameters.QueryParamStyle

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@APIParam(ParameterLocation.query)
annotation class QueryParam(
    val description: String,
    val style: QueryParamStyle = QueryParamStyle.form,
    val explode: Boolean = true,
    val allowEmptyValues: Boolean = false,
    val deprecated: Boolean = false
)
