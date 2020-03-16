package com.papsign.ktor.openapigen.validation.number.floating.max

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.validation.number.NumberConstraint
import com.papsign.ktor.openapigen.validation.number.floating.FloatingNumberConstraintProcessor
import com.papsign.ktor.openapigen.validation.number.floating.clamp.FClamp
import java.math.BigDecimal

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
    override fun getConstraint(annotation: FMax): NumberConstraint {
        return NumberConstraint(max= BigDecimal(annotation.value))
    }
}
