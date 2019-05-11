package com.papsign.ktor.openapigen.annotations.parameters

import com.papsign.ktor.openapigen.openapi.ParameterLocation

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class APIParam(val `in`: ParameterLocation)