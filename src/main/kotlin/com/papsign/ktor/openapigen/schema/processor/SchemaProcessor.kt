package com.papsign.ktor.openapigen.schema.processor

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import kotlin.reflect.KType

interface SchemaProcessor<A: Annotation> {
    fun process(model: SchemaModel<*>, type: KType, annotation: A): SchemaModel<*>
}
