package com.papsign.ktor.openapigen.annotations.properties.description

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.SchemaProcessor
import kotlin.reflect.KType

object DescriptionProcessor: SchemaProcessor<Description> {
    override fun process(model: SchemaModel<*>, type: KType, annotation: Description): SchemaModel<*> {
        model.description = annotation.value
        return model
    }
}
