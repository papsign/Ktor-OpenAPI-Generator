package com.papsign.ktor.openapigen.annotations.type.`object`.example

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.SchemaProcessor
import kotlin.reflect.KType
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.jvm.jvmErasure

object ExampleProcessor : SchemaProcessor<WithExample> {
    override fun process(model: SchemaModel<*>, type: KType, annotation: WithExample): SchemaModel<*> {
        val exampleClass = if (annotation.provider == NoExampleProvider::class) {
            type.jvmErasure.companionObjectInstance as? ExampleProvider<*>
                ?: error("Classes annotated with ${WithExample::class.simpleName} without a specified example provider must have a companion object implementing ${ExampleProvider::class}")
        } else {
            annotation.provider.objectInstance ?: error("Classes extending ${ExampleProvider::class} must be objects")
        }
        @Suppress("UNCHECKED_CAST")
        (model as SchemaModel<Any?>).apply {
            examples = examples?.plus(exampleClass.examples ?: listOf()) ?: exampleClass.examples
            if (example != null) {
                if (exampleClass.example != null)
                    examples = examples?.plus(exampleClass.example)
            } else {
                example = exampleClass.example
            }
        }
        return model
    }

}
