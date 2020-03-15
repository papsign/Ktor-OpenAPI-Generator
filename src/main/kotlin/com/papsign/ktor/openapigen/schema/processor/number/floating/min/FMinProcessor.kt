package com.papsign.ktor.openapigen.schema.processor.number.floating.min

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.number.floating.FloatingNumberConstraintProcessor

object FMinProcessor: FloatingNumberConstraintProcessor<FMin>() {
    override fun process(
        modelLitteral: SchemaModel.SchemaModelLitteral<*>,
        annotation: FMin
    ): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (modelLitteral as SchemaModel.SchemaModelLitteral<Any?>).apply {
            minimum = annotation.value
        }
    }
}
