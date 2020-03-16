package com.papsign.ktor.openapigen.validation.number.floating.min

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.validation.number.NumberConstraint
import com.papsign.ktor.openapigen.validation.number.floating.FloatingNumberConstraintProcessor
import com.papsign.ktor.openapigen.validation.number.floating.clamp.FClamp
import java.math.BigDecimal

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

    override fun getConstraint(annotation: FMin): NumberConstraint {
        return NumberConstraint(min = BigDecimal(annotation.value))
    }
}
