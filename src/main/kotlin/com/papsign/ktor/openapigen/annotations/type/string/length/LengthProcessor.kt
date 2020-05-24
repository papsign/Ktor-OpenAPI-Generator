package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.model.schema.SchemaModel

object LengthProcessor : LengthConstraintProcessor<Length>() {
    override fun process(model: SchemaModel.SchemaModelString, annotation: Length): SchemaModel.SchemaModelString {
        return model.apply {
            maxLength = annotation.max
            minLength = annotation.min
        }
    }

    override fun getConstraint(annotation: Length): LengthConstraint {
        val errorMessage = if (annotation.message.isNotEmpty()) annotation.message else null
        return LengthConstraint(min = annotation.min, max = annotation.max, errorMessage = errorMessage)
    }
}