package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import io.ktor.http.Parameters
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class ObjectBuilder(val type: KType) :
    DeepBuilder {
    private val builderMap: Map<KParameter, DeepBuilder>
    private val constructor: KFunction<Any>

    init {
        val kclass = type.jvmErasure
        if (kclass.isData) {
            constructor = kclass.primaryConstructor ?: error("Parameter objects must have primary constructors")
            builderMap = constructor.parameters.associateWith { parameter -> DeepBuilder.getBuilderForType(parameter.type) }
        } else {
            error("Only data classes are currently supported as parameter objects")
        }
    }

    override fun build(path: String, parameters: Parameters): Any? {
        return constructor.callBy(builderMap.mapValues { it.value.build("$path[${it.key.name}]", parameters) })
    }
}
