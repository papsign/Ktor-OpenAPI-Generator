package com.papsign.ktor.openapigen.generator

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.annotations.parameters.apiParam
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.openapi.Parameter
import com.papsign.ktor.openapigen.openapi.ParameterLocation
import com.papsign.ktor.openapigen.openapi.Schema
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

class ParamBuilder(val apiGen: OpenAPIGen, val provider: ModuleProvider<*>) {

    private val paramAnnotationClasses = hashSetOf(PathParam::class, QueryParam::class)

    fun <T : Any> getParams(t: KClass<T>): List<Parameter<*>> {
        assert(t.isData) { "API route with ${t.simpleName} must be a data class." }
        val constructor = t.primaryConstructor ?: error("API routes with ${t.simpleName} must have a primary constructor.")
        return constructor.parameters.map {
            it.findAnnotation<PathParam>()?.createParam(it) ?:
            it.findAnnotation<QueryParam>()?.createParam(it) ?:
            error("API routes with ${t.simpleName} must have parameters annotated with one of ${paramAnnotationClasses.map { it.simpleName }}")
        }
    }

    fun QueryParam.createParam(param: KParameter): Parameter<*> {
        return createParam(param, apiParam.`in`) {
            it.description = description
            it.allowEmptyValue = allowEmptyValues
            it.deprecated = deprecated
        }
    }

    fun PathParam.createParam(param: KParameter): Parameter<*> {
        return createParam(param, apiParam.`in`) {
            it.description = description
            it.deprecated = deprecated
        }
    }

    private fun createParam(param: KParameter, `in`: ParameterLocation, config: (Parameter<*>) -> Unit): Parameter<*> {
        return Parameter<Any>(
            param.name.toString(),
            `in`,
            !param.type.isMarkedNullable
        ).also {
            it.schema = apiGen.schemaRegistrar[param.type].schema as Schema<Any>
            config(it)
        }
    }
}