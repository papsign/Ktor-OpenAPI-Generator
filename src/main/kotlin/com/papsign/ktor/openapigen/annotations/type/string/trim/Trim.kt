package com.papsign.ktor.openapigen.annotations.type.string.trim

import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@ValidatorAnnotation(TrimValidator::class)
annotation class Trim

