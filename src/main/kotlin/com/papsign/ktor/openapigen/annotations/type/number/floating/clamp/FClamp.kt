package com.papsign.ktor.openapigen.annotations.type.number.floating.clamp

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
@SchemaProcessorAnnotation(FClampProcessor::class)
@ValidatorAnnotation(FClampProcessor::class)
annotation class FClamp(val min: Double, val max: Double, val errorMessage: String = "")


