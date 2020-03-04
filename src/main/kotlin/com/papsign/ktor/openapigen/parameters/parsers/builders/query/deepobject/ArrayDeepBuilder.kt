package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.kotlin.reflection.toKType
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArrayDeepBuilder(type: KType) : CollectionDeepBuilder(type) {

    private val converter = ListToArray(type)

    override fun transform(lst: List<Any?>): Any? {
        return converter.cvt(lst)
    }

    companion object : BuilderSelector<ArrayDeepBuilder> {

        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return type.jvmErasure.java.isArray
        }

        override fun create(type: KType, exploded: Boolean): ArrayDeepBuilder {
            return ArrayDeepBuilder(type)
        }
    }
}
