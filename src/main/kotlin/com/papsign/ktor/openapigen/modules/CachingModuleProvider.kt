package com.papsign.ktor.openapigen.modules

import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

class CachingModuleProvider: ModuleProvider<CachingModuleProvider> {

    @Synchronized
    override fun <T : OpenAPIModule> ofClass(clazz: KClass<T>): Collection<T> {
        return modules[clazz]?.let { it.toSet() as Collection<T> } ?: listOf()
    }

    @Synchronized
    override fun registerModule(module: OpenAPIModule) {
        registerModuleForClass(module::class, module)
        if (module is DependentModule) {
            module.handlers.forEach(this::registerModule)
        }
    }

    @Synchronized
    override fun unRegisterModule(module: OpenAPIModule) {
        modules.values.forEach { it.remove(module) }
    }

    @Synchronized
    private fun registerModuleForClass(clazz: KClass<*>, module: OpenAPIModule) {
        val lst = modules.getOrPut(clazz) {LinkedHashSet()}
        if (!lst.contains(module)) {
            lst.add(module)
            clazz.superclasses.forEach {
                registerModuleForClass(it, module)
            }
        }
    }

    private val modules = HashMap<KClass<*>, LinkedHashSet<OpenAPIModule>>()

    @Synchronized
    override fun child(): CachingModuleProvider {
        val new = CachingModuleProvider()
        modules.forEach { t: KClass<*>, u ->
            new.modules[t] = LinkedHashSet(u)
        }
        return new
    }
}