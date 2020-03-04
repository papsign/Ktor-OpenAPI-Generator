package com.papsign.ktor.openapigen.parameters.parsers.converters.collection

import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

class ListConverter(type: KType) : CollectionConverter(type) {

    override fun transform(lst: List<Any?>): Any? {
        return lst
    }

    companion object : ConverterSelector {
        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.isSubclassOf(List::class)
        }

        override fun create(type: KType): CollectionConverter {
            return ListConverter(type)
        }
    }

}
