package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class ObjectBuilder(val type: KType) : DeepBuilder() {

    private val builderMap: Map<KParameter, DeepBuilder>
    private val constructor: KFunction<Any>

    init {
        val kclass = type.jvmErasure
        if (kclass.isData) {
            constructor = kclass.primaryConstructor ?: error("Parameter objects must have primary constructors")
            builderMap = constructor.parameters.associateWith { parameter -> DeepBuilderFactory.buildBuilder(parameter.type, exploded) ?: error("Could not find DeepObject Builders for type $type") }
        } else {
            error("Only data classes are currently supported for deep objects")
        }
    }

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        return try { constructor.callBy(builderMap.mapValues { it.value.build("$key[${it.key.name}]", parameters) }) } catch (e: InvocationTargetException) { null }
    }

    companion object: BuilderSelector<ObjectBuilder> {

        override fun canHandle(type: KType): Boolean {
            return true
        }

        override fun create(type: KType, exploded: Boolean): ObjectBuilder {
            return ObjectBuilder(type)
        }
    }
}
