package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.model.schema.SchemaModel

object MaxLengthProcessor : LengthConstraintProcessor<MaxLength>() {
    override fun process(model: SchemaModel.SchemaModelString, annotation: MaxLength): SchemaModel.SchemaModelString {
        return model.apply {
            maxLength = annotation.value
        }
    }

    override fun getConstraint(annotation: MaxLength): LengthConstraint {
        val errorMessage = if (annotation.message.isNotEmpty()) annotation.message else null
        return LengthConstraint(max = annotation.value, errorMessage = errorMessage)
    }
}