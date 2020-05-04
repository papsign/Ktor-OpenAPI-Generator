package com.papsign.ktor.openapigen.schema.processor

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
annotation class SchemaProcessorAnnotation(val handler: KClass<out SchemaProcessor<*>>)
