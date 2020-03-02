package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class EnumBuilder(private val enumMap: Map<String, Any?>): DeepBuilder() {

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        return parameters[key]?.let { it[0] }?.let { enumMap[it] }
    }

    companion object: BuilderSelector<EnumBuilder> {
        override fun canHandle(type: KType): Boolean {
            return type.jvmErasure.java.isEnum
        }

        override fun create(type: KType, exploded: Boolean): EnumBuilder {
            return EnumBuilder(type.jvmErasure.java.enumConstants.associateBy { it.toString() })
        }
    }
}
