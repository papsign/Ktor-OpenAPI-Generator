@file:Suppress("UNCHECKED_CAST")

package com.papsign.ktor.openapigen

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.openapi.Schema
import kotlin.reflect.KType


typealias SchemaMap = Map<KType, Schema<*>>

typealias MutableSchemaMap = MutableMap<KType, Schema<*>>

typealias LinkedHashSchemaMap = LinkedHashMap<KType, Schema<*>>
typealias HashSchemaMap = HashMap<KType, Schema<*>>

inline fun <reified T> SchemaMap.get(): Schema<T>? {
    return get(getKType<T>()) as Schema<T>?
}

inline fun <reified T> SchemaMap.containsKey(): Boolean {
    return containsKey(getKType<T>())
}

inline fun <reified T> MutableSchemaMap.put(value: Schema<T>): Schema<T>? {
    return put(getKType<T>(), value) as Schema<T>?
}

inline fun <reified T> MutableSchemaMap.remove(): Schema<T>? {
    return remove(getKType<T>()) as Schema<T>?
}

