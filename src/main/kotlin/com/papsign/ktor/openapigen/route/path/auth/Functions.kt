package com.papsign.ktor.openapigen.route.path.auth

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.route.method
import com.papsign.ktor.openapigen.route.path.normal.route
import com.papsign.ktor.openapigen.route.preHandle
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineAuthContext
import com.papsign.ktor.openapigen.route.throws
import io.ktor.http.HttpMethod
import io.ktor.util.pipeline.ContextDsl
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.get(
    vararg modules: RouteOpenAPIModule,
    exceptions: Collection<APIException<*, *>>? = null,
    example: TResponse? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams) -> Unit
) = route(HttpMethod.Get, modules, exceptions, example, body)

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.post(
    vararg modules: RouteOpenAPIModule,
    exceptions: Collection<APIException<*, *>>? = null,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams, TRequest) -> Unit
) = route(HttpMethod.Post, modules, exceptions, exampleResponse, exampleRequest, body)

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.put(
    vararg modules: RouteOpenAPIModule,
    exceptions: Collection<APIException<*, *>>? = null,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams, TRequest) -> Unit
) = route(HttpMethod.Put, modules, exceptions, exampleResponse, exampleRequest, body)

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.patch(
    vararg modules: RouteOpenAPIModule,
    exceptions: Collection<APIException<*, *>>? = null,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams, TRequest) -> Unit
) = route(HttpMethod.Patch, modules, exceptions, exampleResponse, exampleRequest, body)

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.delete(
    vararg modules: RouteOpenAPIModule,
    exceptions: Collection<APIException<*, *>>? = null,
    example: TResponse? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams) -> Unit
) = route(HttpMethod.Delete, modules, exceptions, example, body)

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.head(
    vararg modules: RouteOpenAPIModule,
    exceptions: Collection<APIException<*, *>>? = null,
    example: TResponse? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams) -> Unit
) = route(HttpMethod.Head, modules, exceptions, example, body)

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exceptions: Collection<APIException<*, *>>? = null,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams, TRequest) -> Unit
) {
    modules.forEach { provider.registerModule(it, it::class.starProjectedType) }
    val route = if (exceptions == null) this else throws(*exceptions.toTypedArray())
    route.method(method).handle(exampleResponse, exampleRequest, body)
}

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exceptions: Collection<APIException<*, *>>? = null,
    exampleResponse: TResponse? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams) -> Unit
) {
    modules.forEach { provider.registerModule(it, it::class.starProjectedType) }
    val route = if (exceptions == null) this else throws(*exceptions.toTypedArray())
    route.method(method).handle(exampleResponse, body)
}

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.handle(
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams, TRequest) -> Unit
) {
    preHandle<TParams, TResponse, TRequest, OpenAPIAuthenticatedRoute<TAuth>>(exampleResponse, exampleRequest) {
        handle(typeOf<TParams>(), typeOf<TResponse>(), typeOf<TRequest>(), body)
    }
}

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, TAuth> OpenAPIAuthenticatedRoute<TAuth>.handle(
    exampleResponse: TResponse? = null,
    noinline body: suspend OpenAPIPipelineAuthContext<TAuth, TResponse>.(TParams) -> Unit
) {
    preHandle<TParams, TResponse, Unit, OpenAPIAuthenticatedRoute<TAuth>>(exampleResponse, Unit) {
        handle(typeOf<TParams>(), typeOf<TResponse>(), body)
    }
}

suspend fun <TAuth> OpenAPIPipelineAuthContext<TAuth, *>.principal() = authProvider.getAuth(pipeline)
