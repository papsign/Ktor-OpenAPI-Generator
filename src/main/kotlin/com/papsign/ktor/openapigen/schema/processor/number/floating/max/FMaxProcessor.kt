package com.papsign.ktor.openapigen.schema.processor.number.floating.max

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.number.floating.FloatingNumberConstraintProcessor

object FMaxProcessor: FloatingNumberConstraintProcessor<FMax>() {
    override fun process(
        modelLitteral: SchemaModel.SchemaModelLitteral<*>,
        annotation: FMax
    ): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (modelLitteral as SchemaModel.SchemaModelLitteral<Any?>).apply {
            maximum = annotation.value
        }
    }
}
