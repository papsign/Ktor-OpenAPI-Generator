package com.papsign.ktor.openapigen.modules

import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.superclasses

class CachingModuleProvider: ModuleProvider<CachingModuleProvider> {

    @Synchronized
    override fun ofType(type: KType): Collection<Any> {
        return modules[type]?.toList() ?: listOf()
    }

    @Synchronized
    override fun registerModule(module: OpenAPIModule, type: KType) {
        registerModuleForClass(type, module)
        if (module is DependentModule) {
            module.handlers.forEach { registerModule(it, it::class.starProjectedType) }
        }
    }

    @Synchronized
    override fun unRegisterModule(module: OpenAPIModule) {
        modules.values.forEach { it.remove(module) }
    }

    @Synchronized
    private fun registerModuleForClass(type: KType, module: OpenAPIModule) {
        val lst = modules.getOrPut(type) {LinkedHashSet()}
        lst.remove(module)
        lst.add(module)
        (type.classifier as KClass<*>).supertypes.forEach {
            registerModuleForClass(it, module)
        }
    }

    private val modules = HashMap<KType, LinkedHashSet<OpenAPIModule>>()

    @Synchronized
    override fun child(): CachingModuleProvider {
        val new = CachingModuleProvider()
        modules.forEach { (t: KType, u) ->
            new.modules[t] = LinkedHashSet(u)
        }
        return new
    }
}
