package com.papsign.ktor.openapigen.annotations.type.number.floating.max

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.annotations.type.number.NumberConstraint
import com.papsign.ktor.openapigen.annotations.type.number.floating.FloatingNumberConstraintProcessor
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
        return NumberConstraint(max= BigDecimal(annotation.value), errorMessage = annotation.errorMessage)
    }
}
