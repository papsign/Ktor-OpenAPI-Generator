package com.papsign.ktor.openapigen.parameters.parsers.converters.collection

import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import com.papsign.ktor.openapigen.parameters.util.ListToArray
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ArrayConverter(type: KType) : CollectionConverter(type) {

    private val converter = ListToArray(type)

    override fun transform(lst: List<Any?>): Any? {
        return converter.cvt(lst)
    }

    companion object : ConverterSelector {
        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.java.isArray
        }

        override fun create(type: KType): CollectionConverter {
            return ArrayConverter(type)
        }
    }
}
