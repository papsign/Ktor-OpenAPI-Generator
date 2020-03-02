package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import kotlin.reflect.KType

abstract class CollectionBuilder: DeepBuilder() {

    protected abstract val contentType: KType
    protected abstract val builder: DeepBuilder

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        val names = parameters.filterKeys { it != key && it.startsWith(key) }
        if (names.isEmpty()) { return listOf<Any?>() }
        val indices =
            names.entries.groupBy { (k, _) -> k.substring(key.length + 1, k.indexOf("]", key.length)).toInt() }
        return indices.entries.fold(
            ArrayList<Any?>((0..(indices.keys.max() ?: 0)).map { null })
        ) { acc, (idx, params) ->
            acc[idx] = builder.build("$key[$idx]", params.associate { (key, value) -> key to value })
            acc
        }
    }
}
