package com.papsign.ktor.openapigen.annotations.type.number.floating.max

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(FMaxProcessor::class)
@ValidatorAnnotation(FMaxProcessor::class)
annotation class FMax(val value: Double, val errorMessage: String = "")
