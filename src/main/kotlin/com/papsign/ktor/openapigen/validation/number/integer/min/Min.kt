package com.papsign.ktor.openapigen.validation.number.integer.min

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation
import com.papsign.ktor.openapigen.validation.string.lowercase.LowerCaseValidator

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(MinProcessor::class)
@ValidatorAnnotation(MinProcessor::class)
annotation class Min(val value: Long)
