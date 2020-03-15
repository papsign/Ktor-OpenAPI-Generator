package com.papsign.ktor.openapigen.schema.processor.number.floating.clamp

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(FClampProcessor::class)
annotation class FClamp(val min: Double, val max: Double)


