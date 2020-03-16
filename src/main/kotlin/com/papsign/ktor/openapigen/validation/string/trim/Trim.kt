package com.papsign.ktor.openapigen.validation.string.trim

import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@ValidatorAnnotation(TrimValidator::class)
annotation class Trim

