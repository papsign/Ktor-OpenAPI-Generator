package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilder
import io.ktor.http.Parameters

class PrimitiveBuilder(val cvt: (String?)->Any?):
    DeepBuilder {
    override fun build(path: String, parameters: Parameters): Any? {
        return cvt(parameters[path])
    }
}
