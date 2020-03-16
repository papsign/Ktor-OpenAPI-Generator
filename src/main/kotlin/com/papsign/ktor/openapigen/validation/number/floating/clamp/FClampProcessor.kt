package com.papsign.ktor.openapigen.validation.number.floating.clamp

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.validation.number.NumberConstraint
import com.papsign.ktor.openapigen.validation.number.floating.FloatingNumberConstraintProcessor
import java.math.BigDecimal

object FClampProcessor : FloatingNumberConstraintProcessor<FClamp>() {
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

    override fun getConstraint(annotation: FClamp): NumberConstraint {
        return NumberConstraint(BigDecimal(annotation.min), BigDecimal(annotation.max))
    }
}
