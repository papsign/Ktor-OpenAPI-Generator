package com.papsign.ktor.openapigen.annotations.type.string.example

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

/**
 * Provide examples for a String property
 */
@Target(AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(StringExampleProcessor::class)
annotation class StringExample(vararg val examples: String)

