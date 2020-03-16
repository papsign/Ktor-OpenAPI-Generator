package com.papsign.ktor.openapigen.validation.number.floating.clamp

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(FClampProcessor::class)
@ValidatorAnnotation(FClampProcessor::class)
annotation class FClamp(val min: Double, val max: Double)


