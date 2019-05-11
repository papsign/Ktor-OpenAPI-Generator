package com.papsign.ktor.openapigen.annotations.parameters

import com.papsign.ktor.openapigen.openapi.ParameterLocation

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@APIParam(ParameterLocation.path)
annotation class PathParam(val description: String, val required: Boolean = true, val deprecated: Boolean = false)