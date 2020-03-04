package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class ListDeepBuilder(type: KType) : CollectionDeepBuilder(type) {

    override fun transform(lst: List<Any?>): Any? {
        return lst
    }

    companion object : BuilderSelector<ListDeepBuilder> {

        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return type.jvmErasure.isSubclassOf(List::class)
        }

        override fun create(type: KType, explode: Boolean): ListDeepBuilder {
            return ListDeepBuilder(type)
        }
    }
}
