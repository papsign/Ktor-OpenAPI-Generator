package com.papsign.ktor.openapigen.schema.processor.number.floating.max

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(FMaxProcessor::class)
annotation class FMax(val value: Double)
