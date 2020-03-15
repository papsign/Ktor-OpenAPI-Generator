package com.papsign.ktor.openapigen.schema.processor.number.integer.min

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(MinProcessor::class)
annotation class Min(val value: Long)
