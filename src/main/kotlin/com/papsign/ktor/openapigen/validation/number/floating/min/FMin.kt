package com.papsign.ktor.openapigen.validation.number.floating.min

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(FMinProcessor::class)
@ValidatorAnnotation(FMinProcessor::class)
annotation class FMin(val value: Double)
