package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import io.ktor.http.Parameters

class EnumBuilder(val enumMap: Map<String, Any?>):
    DeepBuilder {
    override fun build(path: String, parameters: Parameters): Any? {
        return parameters[path]?.let { enumMap[it] }
    }
}
