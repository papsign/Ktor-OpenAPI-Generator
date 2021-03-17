package com.papsign.ktor.openapigen.annotations.type.string.pattern

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@SchemaProcessorAnnotation(RegularExpressionProcessor::class)
@ValidatorAnnotation(RegularExpressionProcessor::class)
annotation class RegularExpression(@org.intellij.lang.annotations.Language("RegExp") val pattern: String, val errorMessage: String = "")

