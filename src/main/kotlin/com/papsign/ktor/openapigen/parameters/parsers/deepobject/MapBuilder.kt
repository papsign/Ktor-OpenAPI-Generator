package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import com.papsign.ktor.openapigen.parameters.util.primitiveParsers
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class MapBuilder(val type: KType) : DeepBuilder() {
    private val keyType = type.arguments[0].type!!
    private val valueType = type.arguments[1].type!!
    private val keyBuilder = primitiveParsers[keyType] ?: error("Only primitives are allowed ")
    private val valueBuilder by lazy { // must be lazy or will recurse infinitely
        DeepBuilderFactory.buildBuilderForced(valueType, exploded)
    }

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        val names = parameters.filterKeys { it.startsWith(key) }
        val indices = names.entries.groupBy { (k, _) -> k.substring(key.length + 1, k.indexOf("]", key.length)) }
        return indices.entries.fold(LinkedHashMap<Any?, Any?>()) { acc, (k, value) ->
            acc[keyBuilder(k)] = valueBuilder.build("$key[$k]", value.associate { (k, v) -> k to v })
            acc
        }
    }

    companion object: BuilderSelector<MapBuilder> {

        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.isSubclassOf(Map::class)
        }

        override fun create(type: KType, exploded: Boolean): MapBuilder {
            return MapBuilder(type)
        }
    }
}
