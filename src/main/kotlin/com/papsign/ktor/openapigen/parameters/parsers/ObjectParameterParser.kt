package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilder
import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilderFactory
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo
import io.ktor.http.Parameters
import io.ktor.util.toMap
import kotlin.reflect.KType

class ObjectParameterParser(info: ParameterInfo, type: KType) : InfoParameterParser(info, {
    when (it) {
        QueryParamStyle.DEFAULT, QueryParamStyle.form -> QueryParamStyle.form to false
        QueryParamStyle.deepObject -> QueryParamStyle.deepObject to true
        else -> error("Query param style $it is undefined for objects in the OpenAPI Spec")
    }
}) {

    private val cvt: (Parameters) -> Any? = when (queryStyle) {
        QueryParamStyle.deepObject -> {
            val builder = DeepBuilderFactory.buildBuilder(type, true)
            ({ builder?.build(key, it.toMap()) })
        }
//                QueryParamStyle.form -> {
//
//                }
        null -> error("Only query params can hold objects")
        else -> error("Query param style $queryStyle is not supported")
    }

    override fun parse(parameters: Parameters): Any? {
        return cvt(parameters)
    }
}

