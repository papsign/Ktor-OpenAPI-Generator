package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(LengthProcessor::class)
@ValidatorAnnotation(LengthProcessor::class)
annotation class MinLength(val value: Int, val message: String = "")