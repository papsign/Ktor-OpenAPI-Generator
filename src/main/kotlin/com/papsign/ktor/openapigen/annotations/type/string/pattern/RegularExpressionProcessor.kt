package com.papsign.ktor.openapigen.annotations.type.string.pattern

import com.papsign.ktor.openapigen.model.schema.SchemaModel

object RegularExpressionProcessor : RegularExpressionConstraintProcessor<RegularExpression>() {
    override fun process(model: SchemaModel.SchemaModelLitteral<*>, annotation: RegularExpression): SchemaModel.SchemaModelLitteral<*> {
        @Suppress("UNCHECKED_CAST")
        return (model as SchemaModel.SchemaModelLitteral<Any?>).apply {
            pattern = annotation.pattern
        }
    }

    override fun getConstraint(annotation: RegularExpression): RegularExpressionConstraint {
        val errorMessage = if (annotation.message.isNotEmpty()) annotation.message else null
        return RegularExpressionConstraint(annotation.pattern, errorMessage)
    }
}