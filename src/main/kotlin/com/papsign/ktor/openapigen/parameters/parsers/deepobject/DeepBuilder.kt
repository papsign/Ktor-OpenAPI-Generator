package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.util.primitiveParsers
import io.ktor.http.Parameters
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

interface DeepBuilder {
    fun build(path: String, parameters: Parameters): Any?

    companion object {
        fun getBuilderForType(type: KType): DeepBuilder {
            primitiveParsers[type]?.let {
                return PrimitiveBuilder(it)
            }
            val clazz = type.jvmErasure
            val jclazz = clazz.java
            return when {
                jclazz.isEnum -> EnumBuilder(jclazz.enumConstants.associateBy { it.toString() })
                clazz.isSubclassOf(List::class) -> ListBuilder(type)
                jclazz.isArray -> error("Nested array are not yet supported")
                clazz.isSubclassOf(Map::class) -> MapBuilder(type)
                else -> ObjectBuilder(type)
            }
        }
    }
}
