package com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArraySpaceDelimitedBuilder(type: KType): CollectionDelimitedBuilder(type, " ") {

    private val converter = ListToArray(type)

    override fun transform(lst: List<Any?>): Any? {
        return converter.cvt(lst)
    }

    companion object : BuilderSelector<ArraySpaceDelimitedBuilder> {

        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return !explode && type.jvmErasure.java.isArray
        }

        override fun create(type: KType, exploded: Boolean): ArraySpaceDelimitedBuilder {
            return ArraySpaceDelimitedBuilder(
                type
            )
        }
    }
}

