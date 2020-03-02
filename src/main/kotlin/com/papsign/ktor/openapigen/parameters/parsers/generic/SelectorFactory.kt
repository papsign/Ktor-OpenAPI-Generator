package com.papsign.ktor.openapigen.parameters.parsers.generic

import com.papsign.ktor.openapigen.parameters.ParameterStyle
import kotlin.reflect.KType

open class SelectorFactory<out T: Builder<S>, S>(vararg val selectors: BuilderSelector<T>): BuilderFactory<T, S> where S: ParameterStyle<S>, S: Enum<S> {
    override fun buildBuilder(type: KType, exploded: Boolean): T? {
        return selectors.find { it.canHandle(type) }?.create(type, exploded)
    }
}
