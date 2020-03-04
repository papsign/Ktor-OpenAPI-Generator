package com.papsign.ktor.openapigen.parameters.util

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.parameters.handlers.ModularParameterHander
import com.papsign.ktor.openapigen.parameters.handlers.ParameterHandler
import com.papsign.ktor.openapigen.parameters.handlers.UnitParameterHandler
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor


inline fun <reified T : Any> buildParameterHandler(): ParameterHandler<T> {
    if (Unit is T) return UnitParameterHandler as ParameterHandler<T>
    val t = T::class
    assert(t.isData) { "API route with ${t.simpleName} must be a data class." }
    val constructor = t.primaryConstructor ?: error("API routes with ${t.simpleName} must have a primary constructor.")
    val parsers: Map<KParameter, Builder<*>> = constructor.parameters.associateWith { param ->
        val type = param.type
        param.findAnnotation<PathParam>()?.let { a -> a.style.factory.buildBuilderForced(type, a.explode) } ?:
        param.findAnnotation<QueryParam>()?.let { a -> a.style.factory.buildBuilderForced(type, a.explode) } ?:
        error("Parameters must be annotated with @PathParam or @QueryParam")
    }
    return ModularParameterHander(
        parsers,
        constructor
    )
}
