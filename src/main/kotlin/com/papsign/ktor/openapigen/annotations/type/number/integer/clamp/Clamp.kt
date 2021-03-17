package com.papsign.ktor.openapigen.annotations.type.number.integer.clamp

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@SchemaProcessorAnnotation(ClampProcessor::class)
@ValidatorAnnotation(ClampProcessor::class)
annotation class Clamp(val min: Long, val max: Long, val errorMessage: String = "")

