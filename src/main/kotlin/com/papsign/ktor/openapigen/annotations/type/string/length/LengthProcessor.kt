package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.model.schema.SchemaModel

object LengthProcessor : LengthConstraintProcessor<Length>() {
    override fun process(model: SchemaModel.SchemaModelLitteral<*>, annotation: Length): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (model as SchemaModel.SchemaModelLitteral<Any?>).apply {
            maxLength = annotation.max
            minLength = annotation.min
        }
    }

    override fun getConstraint(annotation: Length): LengthConstraint {
        return LengthConstraint(min = annotation.min, max = annotation.max, errorMessage = annotation.message)
    }
}