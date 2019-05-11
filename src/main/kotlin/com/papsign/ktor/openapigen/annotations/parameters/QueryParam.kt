package com.papsign.ktor.openapigen.annotations.parameters

import com.papsign.ktor.openapigen.openapi.ParameterLocation

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@APIParam(ParameterLocation.query)
annotation class QueryParam(val description: String, val allowEmptyValues: Boolean = false, val required: Boolean = true, val deprecated: Boolean = false)
