package com.papsign.ktor.openapigen.model

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

interface DataModel {
    fun build(): Map<String, Any?> {
        fun Map<String, *>.clean(): Map<String, *> {
            return filterValues {
                when (it) {
                    is Map<*, *> -> it.isNotEmpty()
                    is Collection<*> -> it.isNotEmpty()
                    else -> it != null
                }
            }
        }
        fun cvt(value: Any?): Any? {
            return when (value) {
                is DataModel -> value.build()
                is Map<*, *> -> value.entries.associate { (key, value) -> Pair(key.toString(), cvt(value)) }.clean()
                is Iterable<*> -> value.map { cvt(it) }
                else -> value
            }
        }
        return this::class.memberProperties.associateBy { it.name }.mapValues { (_, prop) ->
            cvt((prop as KProperty1<DataModel, *>).get(this))
        }.clean()
    }
}
