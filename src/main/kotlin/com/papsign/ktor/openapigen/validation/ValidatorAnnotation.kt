package com.papsign.ktor.openapigen.validation

import com.papsign.ktor.openapigen.validation.ValidatorBuilder
import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
annotation class ValidatorAnnotation(val handler: KClass<out ValidatorBuilder<*>>)
