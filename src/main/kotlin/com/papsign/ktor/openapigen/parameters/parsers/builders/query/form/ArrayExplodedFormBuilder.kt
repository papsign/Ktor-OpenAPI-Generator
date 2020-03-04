package com.papsign.ktor.openapigen.parameters.parsers.builders.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArrayExplodedFormBuilder(type: KType) : CollectionExplodedFormBuilder(type) {

    private val converter = ListToArray(type)

    override fun transform(lst: List<Any?>): Any? {
        return converter.cvt(lst)
    }

    companion object : BuilderSelector<ArrayExplodedFormBuilder> {

        override fun canHandle(type: KType, explode: Boolean): Boolean {
            return type.jvmErasure.java.isArray && explode
        }

        override fun create(type: KType, explode: Boolean): ArrayExplodedFormBuilder {
            return ArrayExplodedFormBuilder(type)
        }
    }
}

