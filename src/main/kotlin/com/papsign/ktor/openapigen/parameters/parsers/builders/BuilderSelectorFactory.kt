package com.papsign.ktor.openapigen.parameters.parsers.builders

import com.papsign.ktor.openapigen.parameters.ParameterStyle
import kotlin.reflect.KType

open class BuilderSelectorFactory<out T: Builder<S>, S>(vararg val selectors: BuilderSelector<T>):
    BuilderFactory<T, S> where S: ParameterStyle<S>, S: Enum<S> {
    override fun buildBuilder(type: KType, explode: Boolean): T? {
        return selectors.find { it.canHandle(type, explode) }?.create(type, explode)
    }
}
