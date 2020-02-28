package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilder.Companion.getBuilderForType
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo
import io.ktor.http.Parameters
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class ObjectParameterParser(info: ParameterInfo, type: KType) : InfoParameterParser(info, {
    when (it) {
        QueryParamStyle.DEFAULT, QueryParamStyle.form -> QueryParamStyle.form to false
        QueryParamStyle.deepObject -> QueryParamStyle.deepObject to true
        else -> error("Query param style $it is undefined for objects in the OpenAPI Spec")
    }
}) {

    private val cvt: (Parameters) -> Any?

    init {
        val kclass = type.jvmErasure
        if (kclass.isData) {
            cvt = when (queryStyle) {
                QueryParamStyle.deepObject -> {
                    val builder = getBuilderForType(type)
                    ({ builder.build(key, it) })
                }
//                QueryParamStyle.form -> {
//
//                }
                null -> error("Only query params can hold objects")
                else -> error("Query param style $queryStyle is not supported")
            }
        } else {
            error("Only data classes are currently supported as parameter objects")
        }
    }


    override fun parse(parameters: Parameters): Any? {
        return cvt(parameters)
    }
}

