package com.papsign.ktor.openapigen.annotations.parameters

import com.papsign.ktor.openapigen.openapi.ParameterLocation
import com.papsign.ktor.openapigen.parameters.PathParamStyle

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@APIParam(ParameterLocation.path)
annotation class PathParam(
    val description: String,
    val style: PathParamStyle = PathParamStyle.simple,
    val explode: Boolean = false,
    val deprecated: Boolean = false
)
