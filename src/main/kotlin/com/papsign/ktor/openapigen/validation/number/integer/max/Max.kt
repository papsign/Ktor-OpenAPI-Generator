package com.papsign.ktor.openapigen.validation.number.integer.max

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(MaxProcessor::class)
@ValidatorAnnotation(MaxProcessor::class)
annotation class Max(val value: Long)
