package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilder.Companion.getBuilderForType
import com.papsign.ktor.openapigen.parameters.util.primitiveParsers
import io.ktor.http.Parameters
import kotlin.reflect.KType

class MapBuilder(val type: KType) :
    DeepBuilder {
    private val keyType = type.arguments[0].type!!
    private val valueType = type.arguments[0].type!!
    private val keyBuilder = primitiveParsers[keyType] ?: error("Only primitives are allowed ")
    private val valueBuilder = getBuilderForType(valueType)
    override fun build(path: String, parameters: Parameters): Any? {
        val names = parameters.names().filter { it.startsWith(path) }.toSet()
        val indices =
            names.map { it.substring(path.length + 1, it.indexOf("]", path.length)) }.associateWith { keyBuilder(it) }
        return indices.entries.fold(LinkedHashMap<Any?, Any?>()) { acc, (key, value) ->
            acc[value] = valueBuilder.build("$path[$key]", parameters)
            acc
        }
    }
}
