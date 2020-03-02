package com.papsign.ktor.openapigen.parameters.parsers.generic

import kotlin.reflect.KType

interface BuilderSelector<out T: Builder<*>> {
    fun canHandle(type: KType): Boolean
    fun create(type: KType, exploded: Boolean): T
}
