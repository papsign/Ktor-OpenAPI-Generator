package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class ListBuilder(val type: KType) : CollectionBuilder() {
    override val contentType = type.arguments[0].type!!
    override val builder by lazy { // must be lazy or will recurse infinitely
        DeepBuilderFactory.buildBuilder(contentType, exploded) ?: error("No DeepBuilder exists for type $type")
    }

    companion object : BuilderSelector<ListBuilder> {

        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.isSubclassOf(List::class)
        }

        override fun create(type: KType, exploded: Boolean): ListBuilder {
            return ListBuilder(type)
        }
    }
}
