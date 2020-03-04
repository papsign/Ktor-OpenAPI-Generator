package com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class ListPipeDelimitedBuilder(type: KType) : CollectionDelimitedBuilder(type, "|") {

    override fun transform(lst: List<Any?>): Any? {
        return lst
    }

    companion object : BuilderSelector<ListPipeDelimitedBuilder> {

        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return !explode && type.jvmErasure.isSubclassOf(List::class)
        }

        override fun create(type: KType, exploded: Boolean): ListPipeDelimitedBuilder {
            return ListPipeDelimitedBuilder(
                type
            )
        }
    }
}
