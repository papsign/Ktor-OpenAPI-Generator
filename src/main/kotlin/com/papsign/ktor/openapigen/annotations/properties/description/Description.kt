package com.papsign.ktor.openapigen.annotations.properties.description

import com.papsign.ktor.openapigen.schema.processor.SchemaProcessorAnnotation

@Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@SchemaProcessorAnnotation(DescriptionProcessor::class)
annotation class Description(val value: String)
