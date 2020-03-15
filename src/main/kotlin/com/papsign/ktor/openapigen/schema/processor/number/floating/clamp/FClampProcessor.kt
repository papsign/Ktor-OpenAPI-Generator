package com.papsign.ktor.openapigen.schema.processor.number.floating.clamp

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.number.floating.FloatingNumberConstraintProcessor

object FClampProcessor: FloatingNumberConstraintProcessor<FClamp>() {
    override fun process(
        modelLitteral: SchemaModel.SchemaModelLitteral<*>,
        annotation: FClamp
    ): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (modelLitteral as SchemaModel.SchemaModelLitteral<Any?>).apply {
            minimum = annotation.min
            maximum = annotation.max
        }
    }
}
