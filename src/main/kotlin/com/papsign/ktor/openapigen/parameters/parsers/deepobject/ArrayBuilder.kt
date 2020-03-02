package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.kotlin.reflection.toKType
import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import com.papsign.ktor.openapigen.parameters.util.primCVT
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArrayBuilder(val type: KType) : CollectionBuilder() {
    private val componentType = type.jvmErasure.java.componentType
    override val contentType = componentType.toKType()

    override val builder by lazy { // must be lazy or it will recurse infinitely
        DeepBuilderFactory.buildBuilderForced(contentType, exploded)
    }

    private fun cvt(list: List<Any?>): Any? {
        val arr = java.lang.reflect.Array.newInstance(componentType, list.size)
        list.forEachIndexed { index, elem ->
            java.lang.reflect.Array.set(arr, index, elem)
        }
        return arr
    }

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        return cvt(super.build(key, parameters) as List<Any?>)
    }

    companion object : BuilderSelector<ArrayBuilder> {

        private val primCVT = mapOf(
            primCVT<Long> { it.toLongArray() },
            primCVT<Int> { it.toIntArray() },
            primCVT<Float> { it.toFloatArray() },
            primCVT<Double> { it.toDoubleArray() },
            primCVT<Boolean> { it.toBooleanArray() }
        )

        /**
         * you may think it is redundant but it is not. Maybe the nullable types are useless though.
         */
        private val arrCVT = mapOf(
            primCVT<Long> { it.toTypedArray() },
            primCVT<Int> { it.toTypedArray() },
            primCVT<Float> { it.toTypedArray() },
            primCVT<Double> { it.toTypedArray() },
            primCVT<Boolean> { it.toTypedArray() }
        )

        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.java.isArray
        }

        override fun create(type: KType, exploded: Boolean): ArrayBuilder {
            return ArrayBuilder(type)
        }
    }
}
