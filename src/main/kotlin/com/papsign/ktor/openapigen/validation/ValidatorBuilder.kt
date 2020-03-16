package com.papsign.ktor.openapigen.validation

import kotlin.reflect.KType

interface ValidatorBuilder<A: Annotation> {
    val exceptionTypes: List<KType>
        get() = listOf()
    fun build(type: KType, annotation: A): Validator
}
