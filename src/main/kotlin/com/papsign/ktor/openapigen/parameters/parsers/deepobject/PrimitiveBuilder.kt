package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import com.papsign.ktor.openapigen.parameters.util.primitiveParsers
import kotlin.reflect.KType

class PrimitiveBuilder(val cvt: (String?)->Any?): DeepBuilder() {

    override fun build(key: String, parameters: Map<String, List<String>>): Any? {
        return parameters[key]?.let { it[0] }?.let(cvt)
    }

    companion object: BuilderSelector<PrimitiveBuilder> {
        override fun canHandle(type: KType): Boolean {
            return primitiveParsers.containsKey(type)
        }

        override fun create(type: KType, exploded: Boolean): PrimitiveBuilder {
            return PrimitiveBuilder(primitiveParsers[type] ?: error("Primitive of type $type does not exist"))
        }
    }
}
