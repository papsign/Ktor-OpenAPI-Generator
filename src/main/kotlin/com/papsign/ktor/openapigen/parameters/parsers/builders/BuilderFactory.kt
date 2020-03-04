package com.papsign.ktor.openapigen.parameters.parsers.builders

import com.papsign.ktor.openapigen.parameters.ParameterStyle
import kotlin.reflect.KType

interface BuilderFactory<out T: Builder<S>, S> where S: ParameterStyle<S>, S: Enum<S> {
    fun buildBuilder(type: KType, explode: Boolean): T?
    fun buildBuilderForced(type: KType, explode: Boolean): T = buildBuilder(type, explode) ?: error("No ${this.javaClass.declaringClass?.simpleName ?: this.javaClass.simpleName} Builder exists for type $type")
}
