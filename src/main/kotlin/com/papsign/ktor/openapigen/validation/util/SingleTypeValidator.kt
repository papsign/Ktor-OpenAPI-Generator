package com.papsign.ktor.openapigen.validation.util

import com.papsign.ktor.openapigen.validation.Validator
import com.papsign.ktor.openapigen.validation.ValidatorBuilder
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

open class SingleTypeValidator<A: Annotation>(allowedType: KType, private val validator: (A)-> Validator): ValidatorBuilder<A> {
    private val allowedType: KType = allowedType.withNullability(false)
    override fun build(type: KType, annotation: A): Validator {
        if (type.withNullability(false) == allowedType) return validator(annotation)
        error("${annotation::class} annotation cannot be applied to type: $type, only $allowedType is allowed")
    }
}
