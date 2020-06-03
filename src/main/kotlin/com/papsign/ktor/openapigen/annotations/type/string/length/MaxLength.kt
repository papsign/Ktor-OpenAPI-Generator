package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(MaxLengthProcessor::class)
@ValidatorAnnotation(MaxLengthProcessor::class)
annotation class MaxLength(val value: Int, val errorMessage: String = "")