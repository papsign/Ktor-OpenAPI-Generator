package com.papsign.ktor.openapigen.validation

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
annotation class ValidatorAnnotation(val handler: KClass<out ValidatorBuilder<*>>)
