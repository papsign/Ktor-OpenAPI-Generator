package com.papsign.ktor.openapigen.schema.processor.number.integer.clamp

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.number.integer.IntegerNumberConstraintProcessor

object ClampProcessor: IntegerNumberConstraintProcessor<Clamp>() {
    override fun process(
        modelLitteral: SchemaModel.SchemaModelLitteral<*>,
        annotation: Clamp
    ): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (modelLitteral as SchemaModel.SchemaModelLitteral<Any?>).apply {
            minimum = annotation.min
            maximum = annotation.max
        }
    }
}
