package com.papsign.ktor.openapigen.validation.number.integer.min

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.validation.number.NumberConstraint
import com.papsign.ktor.openapigen.validation.number.floating.min.FMin
import com.papsign.ktor.openapigen.validation.number.integer.IntegerNumberConstraintProcessor
import java.math.BigDecimal

object MinProcessor: IntegerNumberConstraintProcessor<Min>() {
    override fun process(
        modelLitteral: SchemaModel.SchemaModelLitteral<*>,
        annotation: Min
    ): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (modelLitteral as SchemaModel.SchemaModelLitteral<Any?>).apply {
            minimum = annotation.value
        }
    }

    override fun getConstraint(annotation: Min): NumberConstraint {
        return NumberConstraint(min = BigDecimal(annotation.value))
    }
}
