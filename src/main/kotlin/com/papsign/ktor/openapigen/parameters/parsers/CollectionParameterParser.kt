package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo
import com.papsign.ktor.openapigen.parameters.util.primitiveParsers
import io.ktor.http.Parameters
import kotlin.reflect.KType

class CollectionParameterParser<T, A>(info: ParameterInfo, type: KType, val cvt: (List<T>?) -> A) :
    InfoParameterParser(info, { style ->
        when (style) {
            QueryParamStyle.DEFAULT -> QueryParamStyle.form
            QueryParamStyle.deepObject -> error("Deep Objects are not supported for Arrays")
            else -> style
        }
    }) {

    private val parseFunc: (Parameters) -> A

    init {
        val typeParser = primitiveParsers[type] as ((String?) -> T)? ?: error("Non-primitive Arrays aren't yet supported")
        val explodedParse = ({ parameters: Parameters -> cvt(parameters.getAll(key)?.map(typeParser)) })
        parseFunc = when (queryStyle) {
            QueryParamStyle.form -> {
                if (explode) {
                    explodedParse
                } else {
                    ({ parameters: Parameters -> cvt(parameters[key]?.split(',')?.map(typeParser)) })
                }
            }
            QueryParamStyle.pipeDelimited -> {
                if (explode) {
                    explodedParse
                } else {
                    ({ parameters: Parameters -> cvt(parameters[key]?.split('|')?.map(typeParser)) })
                }
            }
            QueryParamStyle.spaceDelimited -> {
                if (explode) {
                    explodedParse
                } else {
                    ({ parameters: Parameters -> cvt(parameters[key]?.split(' ')?.map(typeParser)) })
                }
            }
            null -> null
            else -> error("Query param style $queryStyle is not supported for collections")
        } ?: when (pathStyle) {
            PathParamStyle.simple -> ({ parameters: Parameters -> cvt(parameters[key]?.split(',')?.map(typeParser)) })
            PathParamStyle.label -> {
                if (explode) {
                    ({ parameters: Parameters -> cvt(parameters[key]?.split('.')?.drop(1)?.map(typeParser)) })
                } else {
                    ({ parameters: Parameters -> cvt(parameters[key]?.removePrefix(".")?.split(',')?.map(typeParser)) })
                }
            }
            PathParamStyle.matrix -> {
                if (explode) {
                    ({ parameters: Parameters -> cvt(parameters[key]?.split(";$key=")?.drop(1)?.map(typeParser)) })
                } else {
                    ({ parameters: Parameters -> cvt(parameters[key]?.removePrefix(";$key=")?.split(',')?.map(typeParser)) })
                }
            }
            else -> error("Path param style $pathStyle is not supported for collections")
        }
    }

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters)
    }
}
