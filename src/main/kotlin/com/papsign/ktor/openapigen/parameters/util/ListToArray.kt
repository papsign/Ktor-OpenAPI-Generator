package com.papsign.ktor.openapigen.parameters.util

import com.papsign.kotlin.reflection.toKType
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ListToArray(arrayType: KType) {

    private val type: Class<*> = arrayComponentClass(arrayType)

    fun cvt(list: List<Any?>): Any? {
        val arr = java.lang.reflect.Array.newInstance(type, list.size)
        list.forEachIndexed { index, elem ->
            java.lang.reflect.Array.set(arr, index, elem)
        }
        return arr
    }

    companion object {
        fun arrayComponentKType(arrayType: KType): KType {
            return arrayType.arguments.firstOrNull()?.type ?: arrayType.jvmErasure.java.componentType.toKType()
        }
        fun arrayComponentClass(arrayType: KType): Class<*> {
            return arrayType.arguments.firstOrNull()?.type?.jvmErasure?.javaObjectType ?:
            arrayType.jvmErasure.java.componentType.toKType().jvmErasure.javaPrimitiveType ?:
            Any::class.java
        }
    }
}
