package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(LengthProcessor::class)
@ValidatorAnnotation(LengthProcessor::class)
annotation class Length(val min: Int, val max: Int, val errorMessage: String = "")
