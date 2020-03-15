package com.papsign.ktor.openapigen.schema.processor.number.integer.max

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE)
@SchemaProcessorAnnotation(MaxProcessor::class)
annotation class Max(val value: Long)
