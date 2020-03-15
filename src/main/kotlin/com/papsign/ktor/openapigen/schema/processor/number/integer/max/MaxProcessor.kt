package com.papsign.ktor.openapigen.schema.processor.number.integer.max

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.number.integer.IntegerNumberConstraintProcessor

object MaxProcessor: IntegerNumberConstraintProcessor<Max>() {
    override fun process(
        modelLitteral: SchemaModel.SchemaModelLitteral<*>,
        annotation: Max
    ): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (modelLitteral as SchemaModel.SchemaModelLitteral<Any?>).apply {
            maximum = annotation.value
        }
    }
}
