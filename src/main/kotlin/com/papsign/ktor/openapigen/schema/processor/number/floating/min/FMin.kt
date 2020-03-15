package com.papsign.ktor.openapigen.schema.processor.number.floating.min

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(FMinProcessor::class)
annotation class FMin(val value: Double)
