package com.papsign.ktor.openapigen.parameters.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.annotations.parameters.apiParam
import com.papsign.ktor.openapigen.model.operation.ParameterLocation
import com.papsign.ktor.openapigen.model.operation.ParameterModel
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.schema.builder.provider.FinalSchemaBuilderProviderModule
import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.util.toMap
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.withNullability

class ModularParameterHander<T>(val parsers: Map<KParameter, Builder<*>>, val constructor: KFunction<T>) :
    ParameterHandler<T> {

    override fun parse(parameters: Parameters, headers: Headers): T {
        return constructor.callBy(parsers.mapValues { it.value.build(it.key.name!!, parameters.toMap() + headers.toMap()) })
    }

    override fun getParameters(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<ParameterModel<*>> {
        val schemaBuilder = provider.ofClass<FinalSchemaBuilderProviderModule>().last().provide(apiGen, provider)

        fun createParam(param: KParameter, `in`: ParameterLocation, config: (ParameterModel<*>) -> Unit): ParameterModel<*> {
            return ParameterModel<Any>(
                param.name.toString(),
                `in`,
                !param.type.isMarkedNullable
            ).also {
                @Suppress("UNCHECKED_CAST")
                it.schema = schemaBuilder.build(param.type.withNullability(false)) as SchemaModel<Any>
                config(it)
            }
        }

        fun HeaderParam.createParam(param: KParameter): ParameterModel<*> {
            val parser = parsers[param]!!
            return createParam(param, apiParam.`in`) {
                it.description = description
                it.allowEmptyValue = allowEmptyValues
                it.deprecated = deprecated
                it.style = parser.style
                it.explode = parser.explode
            }
        }

        fun QueryParam.createParam(param: KParameter): ParameterModel<*> {
            val parser = parsers[param]!!
            return createParam(param, apiParam.`in`) {
                it.description = description
                it.allowEmptyValue = allowEmptyValues
                it.deprecated = deprecated
                it.style = parser.style
                it.explode = parser.explode
            }
        }

        fun PathParam.createParam(param: KParameter): ParameterModel<*> {
            val parser = parsers[param]!!
            return createParam(param, apiParam.`in`) {
                it.description = description
                it.deprecated = deprecated
                it.style = parser.style
                it.explode = parser.explode
            }
        }

        return constructor.parameters.map {
            it.findAnnotation<HeaderParam>()?.createParam(it) ?:
            it.findAnnotation<PathParam>()?.createParam(it) ?:
            it.findAnnotation<QueryParam>()?.createParam(it) ?:
            error("API routes with ${constructor.returnType} must have parameters annotated with one of ${paramAnnotationClasses.map { it.simpleName }}")
        }
    }

    companion object {
        private val paramAnnotationClasses = hashSetOf(HeaderParam::class, PathParam::class, QueryParam::class)
    }
}
