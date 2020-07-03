package com.papsign.ktor.openapigen.parameters.util

import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.parameters.handlers.ModularParameterHandler
import com.papsign.ktor.openapigen.parameters.handlers.ParameterHandler
import com.papsign.ktor.openapigen.parameters.handlers.UnitParameterHandler
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor


inline fun <T : Any> buildParameterHandler(tClass: KClass<T>): ParameterHandler<T> {
    if (tClass == Unit::class) return UnitParameterHandler as ParameterHandler<T>
    assert(tClass.isData) { "API route with ${tClass.simpleName} must be a data class." }
    val constructor = tClass.primaryConstructor ?: error("API routes with ${tClass.simpleName} must have a primary constructor.")
    val parsers: Map<KParameter, Builder<*>> = constructor.parameters.associateWith { param ->
        val type = param.type
        param.findAnnotation<HeaderParam>()?.let { a -> a.style.factory.buildBuilderForced(type, a.explode) } ?:
        param.findAnnotation<PathParam>()?.let { a -> a.style.factory.buildBuilderForced(type, a.explode) } ?:
        param.findAnnotation<QueryParam>()?.let { a -> a.style.factory.buildBuilderForced(type, a.explode) } ?:
        error("Parameters must be annotated with @PathParam or @QueryParam")
    }
    return ModularParameterHandler(
        parsers,
        constructor
    )
}
