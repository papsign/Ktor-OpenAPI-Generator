package com.papsign.ktor.openapigen.annotations.type.`object`.example

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import kotlin.reflect.KClass

/**
 * Careful, no type checking done if you give the wrong provider
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@SchemaProcessorAnnotation(ExampleProcessor::class)
annotation class WithExample(val provider: KClass<out ExampleProvider<*>> = NoExampleProvider::class)


