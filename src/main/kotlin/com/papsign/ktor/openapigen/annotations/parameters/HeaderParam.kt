package com.papsign.ktor.openapigen.annotations.parameters

import com.papsign.ktor.openapigen.model.operation.ParameterLocation
import com.papsign.ktor.openapigen.parameters.HeaderParamStyle

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@APIParam(ParameterLocation.header)
annotation class HeaderParam(
    val description: String,
    val style: HeaderParamStyle = HeaderParamStyle.simple,
    val explode: Boolean = true,
    val allowEmptyValues: Boolean = false,
    val deprecated: Boolean = false
)
