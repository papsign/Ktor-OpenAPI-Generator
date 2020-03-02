package com.papsign.ktor.openapigen.parameters.parsers.generic

import com.papsign.ktor.openapigen.parameters.ParameterStyle
import kotlin.reflect.KType

interface BuilderFactory<out T: Builder<S>, S> where S: ParameterStyle<S>, S: Enum<S> {
    fun buildBuilder(type: KType, exploded: Boolean): T?
    fun buildBuilderForced(type: KType, exploded: Boolean): T = buildBuilder(type, exploded) ?: error("No ${this.javaClass.declaringClass?.simpleName ?: this.javaClass.simpleName} Builder exists for type $type")
}
