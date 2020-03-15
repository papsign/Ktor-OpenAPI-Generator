package com.papsign.ktor.openapigen.schema.processor.number.integer.clamp

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(ClampProcessor::class)
annotation class Clamp(val min: Long, val max: Long)
