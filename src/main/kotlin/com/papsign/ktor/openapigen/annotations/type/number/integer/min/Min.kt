package com.papsign.ktor.openapigen.annotations.type.number.integer.min

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@SchemaProcessorAnnotation(MinProcessor::class)
@ValidatorAnnotation(MinProcessor::class)
annotation class Min(val value: Long, val errorMessage: String = "")

