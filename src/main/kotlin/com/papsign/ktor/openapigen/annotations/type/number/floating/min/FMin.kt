package com.papsign.ktor.openapigen.annotations.type.number.floating.min

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(FMinProcessor::class)
@ValidatorAnnotation(FMinProcessor::class)
annotation class FMin(val value: Double, val errorMessage: String = "")
