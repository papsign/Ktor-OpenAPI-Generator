package com.papsign.ktor.openapigen.modules

import kotlin.reflect.KClass

interface ModuleProvider<THIS: ModuleProvider<THIS>> {
    fun <T: OpenAPIModule> ofClass(clazz: KClass<T>): Collection<T>
    fun registerModule(module: OpenAPIModule)
    fun unRegisterModule(module: OpenAPIModule)
    fun child(): THIS
}

inline fun <reified T: OpenAPIModule> ModuleProvider<*>.ofClass(): Collection<T> {
    return ofClass(T::class)
}