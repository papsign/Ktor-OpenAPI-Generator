package com.papsign.ktor.openapigen.validation.number.integer.clamp

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(ClampProcessor::class)
@ValidatorAnnotation(ClampProcessor::class)
annotation class Clamp(val min: Long, val max: Long)
