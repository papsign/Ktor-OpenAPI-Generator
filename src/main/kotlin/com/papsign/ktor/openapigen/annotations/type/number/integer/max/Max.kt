package com.papsign.ktor.openapigen.annotations.type.number.integer.max

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(MaxProcessor::class)
@ValidatorAnnotation(MaxProcessor::class)
annotation class Max(val value: Long)
