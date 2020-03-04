package com.papsign.ktor.openapigen.parameters.parsers.builders

import kotlin.reflect.KType

interface BuilderSelector<out T: Builder<*>> {
    fun canHandle(type: KType, explode: Boolean): Boolean
    fun create(type: KType, exploded: Boolean): T
}
