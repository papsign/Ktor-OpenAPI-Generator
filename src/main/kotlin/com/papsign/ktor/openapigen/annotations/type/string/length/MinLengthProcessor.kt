package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.model.schema.SchemaModel

object MinLengthProcessor : LengthConstraintProcessor<MinLength>() {
    override fun process(model: SchemaModel.SchemaModelString, annotation: MinLength): SchemaModel.SchemaModelString {
        return model.apply {
            minLength = annotation.value
        }
    }

    override fun getConstraint(annotation: MinLength): LengthConstraint {
        val errorMessage = if (annotation.message.isNotEmpty()) annotation.message else null
        return LengthConstraint(min = annotation.value, errorMessage = errorMessage)
    }
}