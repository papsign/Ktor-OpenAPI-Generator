package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilder.Companion.getBuilderForType
import io.ktor.http.Parameters
import kotlin.reflect.KType

class ListBuilder(val type: KType):
    DeepBuilder {
    private val contentType = type.arguments[0].type!!
    private val builder = getBuilderForType(contentType)
    override fun build(path: String, parameters: Parameters): Any? {
        val names = parameters.names().filter { it.startsWith(path) }.toSet()
        val indices = names.map { it.substring(path.length + 1, it.indexOf("]", path.length)).toInt() }.toSet()
        return indices.fold(ArrayList<Any?>().also { it.ensureCapacity(indices.max()?:0) }) { acc, idx ->
            acc[idx] = builder.build("$path[$idx]", parameters)
            acc
        }
    }
}
