package com.papsign.ktor.openapigen.validation.number.integer.clamp

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.validation.number.NumberConstraint
import com.papsign.ktor.openapigen.validation.number.floating.clamp.FClamp
import com.papsign.ktor.openapigen.validation.number.integer.IntegerNumberConstraintProcessor
import java.math.BigDecimal

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

    override fun getConstraint(annotation: Clamp): NumberConstraint {
        return NumberConstraint(BigDecimal(annotation.min), BigDecimal(annotation.max))
    }
}
