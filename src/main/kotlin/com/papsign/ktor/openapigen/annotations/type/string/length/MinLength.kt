package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(MinLengthProcessor::class)
@ValidatorAnnotation(MinLengthProcessor::class)
annotation class MinLength(val value: Int, val errorMessage: String = "")