package com.papsign.ktor.openapigen.annotations.type.string.example

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.SchemaProcessor
import kotlin.reflect.KType

object StringExampleProcessor: SchemaProcessor<StringExample> {
    override fun process(model: SchemaModel<*>, type: KType, annotation: StringExample): SchemaModel<*> {
        (model as SchemaModel<String?>).apply {
            if (annotation.examples.size > 1) {
                examples = examples?.plus(annotation.examples) ?: annotation.examples.asList()
            } else {
                if (example == null) {
                    example = annotation.examples.getOrNull(0)
                } else {
                    examples = examples?.plus(annotation.examples)
                }
            }
        }
        return model
    }

}
