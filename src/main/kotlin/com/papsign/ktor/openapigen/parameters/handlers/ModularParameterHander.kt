package com.papsign.ktor.openapigen.parameters.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.annotations.parameters.apiParam
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.openapi.Parameter
import com.papsign.ktor.openapigen.openapi.ParameterLocation
import com.papsign.ktor.openapigen.openapi.Schema
import com.papsign.ktor.openapigen.parameters.parsers.ParameterParser
import io.ktor.http.Parameters
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

class ModularParameterHander<T>(val parsers: Map<KParameter, ParameterParser>, val constructor: KFunction<T>) :
    ParameterHandler<T> {

    override fun parse(parameters: Parameters): T {
        return constructor.callBy(parsers.mapValues { it.value.parse(parameters) })
    }

    override fun getParameters(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<Parameter<*>> {

        fun createParam(param: KParameter, `in`: ParameterLocation, config: (Parameter<*>) -> Unit): Parameter<*> {
            return Parameter<Any>(
                param.name.toString(),
                `in`,
                !param.type.isMarkedNullable
            ).also {
                it.schema = apiGen.schemaRegistrar[param.type].schema as Schema<Any>
                config(it)
            }
        }

        fun QueryParam.createParam(param: KParameter): Parameter<*> {
            val parser = parsers[param]!!
            return createParam(param, apiParam.`in`) {
                it.description = description
                it.allowEmptyValue = allowEmptyValues
                it.deprecated = deprecated
                it.style = parser.queryStyle
                it.explode = parser.explode
            }
        }

        fun PathParam.createParam(param: KParameter): Parameter<*> {
            val parser = parsers[param]!!
            return createParam(param, apiParam.`in`) {
                it.description = description
                it.deprecated = deprecated
                it.style = parser.pathStyle
                it.explode = parser.explode
            }
        }

        return constructor.parameters.map {
            it.findAnnotation<PathParam>()?.createParam(it) ?:
            it.findAnnotation<QueryParam>()?.createParam(it) ?:
            error("API routes with ${constructor.returnType} must have parameters annotated with one of ${paramAnnotationClasses.map { it.simpleName }}")
        }
    }

    companion object {
        private val paramAnnotationClasses = hashSetOf(PathParam::class, QueryParam::class)
    }
}
