package com.papsign.ktor.openapigen.validation.string.lowercase

import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.TYPE)
@ValidatorAnnotation(LowerCaseValidator::class)
annotation class LowerCase

