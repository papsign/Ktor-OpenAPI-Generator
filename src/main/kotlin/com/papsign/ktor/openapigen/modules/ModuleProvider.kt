package com.papsign.ktor.openapigen.modules

import com.papsign.ktor.openapigen.getKType
import kotlin.reflect.KType

interface ModuleProvider<THIS: ModuleProvider<THIS>> {
    fun ofType(type: KType): Collection<Any>
    fun registerModule(module: OpenAPIModule, type: KType)
    fun unRegisterModule(module: OpenAPIModule)
    fun child(): THIS
}

inline fun <reified T: OpenAPIModule> ModuleProvider<*>.ofType(): Collection<T> =
    ofType(getKType<T>()) as Collection<T>

inline fun <reified T: OpenAPIModule> ModuleProvider<*>.registerModule(module: T) =
    registerModule(module, getKType<T>())
