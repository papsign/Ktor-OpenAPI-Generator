package com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class ListSpaceDelimitedBuilder(type: KType) : CollectionDelimitedBuilder(type, " ") {

    override fun transform(lst: List<Any?>): Any? {
        return lst
    }

    companion object : BuilderSelector<ListSpaceDelimitedBuilder> {

        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return !explode && type.jvmErasure.isSubclassOf(List::class)
        }

        override fun create(type: KType, explode: Boolean): ListSpaceDelimitedBuilder {
            return ListSpaceDelimitedBuilder(
                type
            )
        }
    }
}
