package com.papsign.ktor.openapigen.modules

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf

class CachingModuleProvider(previous: Iterable<Pair<KType, OpenAPIModule>> = listOf()) : ModuleProvider<CachingModuleProvider> {

    private val modules: MutableList<Pair<KType, OpenAPIModule>> = Collections.synchronizedList( synchronized(previous) { previous.toMutableList() } )

    override fun ofType(type: KType): Collection<OpenAPIModule> {
        val set = LinkedHashSet<OpenAPIModule>()
        synchronized(modules) {
            modules.filter {
                it.first.isSubtypeOf(type)
            }
        }.forEach {
            set.remove(it.second)
            set.add(it.second)
        }
        return set
    }

    override fun registerModule(module: OpenAPIModule, type: KType) {
        if (module is DependentModule) {
            module.handlers.forEach { (depType, depModule) ->
                if (synchronized(modules) { modules.find { it.second == depModule } } == null) {
                    registerModule(depModule, depType)
                }
            }
        }
        modules.add(type to module)
    }

    override fun unRegisterModule(module: OpenAPIModule) {
        synchronized(modules) { modules.removeIf { it.second == module } }
    }

    override fun child(): CachingModuleProvider {
        return CachingModuleProvider(modules)
    }
}
