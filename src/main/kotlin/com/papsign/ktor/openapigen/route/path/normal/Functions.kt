package com.papsign.ktor.openapigen.route.path.normal

import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.route.method
import com.papsign.ktor.openapigen.route.preHandle
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import io.ktor.http.HttpMethod
import io.ktor.util.pipeline.ContextDsl
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf

/**
 * Builds a route to match `GET` requests generating OpenAPI documentation.
 * Get parameters will have the type [TParams] and response type will have [TResponse].
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param example an example of [TResponse] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any> NormalOpenAPIRoute.get(
    vararg modules: RouteOpenAPIModule,
    example: TResponse? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams) -> Unit
) = route(HttpMethod.Get, modules, example, body)

/**
 * Builds a route to match `POST` requests generating OpenAPI documentation.
 * Route parameters will have the type [TParams] and response type will have [TResponse].
 * The body of the request will be parsed into a [TRequest] instance.
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param exampleResponse optional example of [TResponse] to add to OpenAPI specification
 * @param exampleRequest optional example of [TRequest] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any> NormalOpenAPIRoute.post(
    vararg modules: RouteOpenAPIModule,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams, TRequest) -> Unit
) = route(HttpMethod.Post, modules, exampleResponse, exampleRequest, body)

/**
 * Builds a route to match `PUT` requests generating OpenAPI documentation.
 * Route parameters will have the type [TParams] and response type will have [TResponse].
 * The body of the request will be parsed into a [TRequest] instance.
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param exampleResponse optional example of [TResponse] to add to OpenAPI specification
 * @param exampleRequest optional example of [TRequest] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any> NormalOpenAPIRoute.put(
    vararg modules: RouteOpenAPIModule,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams, TRequest) -> Unit
) = route(HttpMethod.Put, modules, exampleResponse, exampleRequest, body)

/**
 * Builds a route to match `PATCH` requests generating OpenAPI documentation.
 * Route parameters will have the type [TParams] and response type will have [TResponse].
 * The body of the request will be parsed into a [TRequest] instance.
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param exampleResponse optional example of [TResponse] to add to OpenAPI specification
 * @param exampleRequest optional example of [TRequest] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any> NormalOpenAPIRoute.patch(
    vararg modules: RouteOpenAPIModule,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams, TRequest) -> Unit
) = route(HttpMethod.Patch, modules, exampleResponse, exampleRequest, body)

/**
 * Builds a route to match `DELETE` requests generating OpenAPI documentation.
 * Route parameters will have the type [TParams] and response type will have [TResponse].
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param example optional example of [TResponse] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any> NormalOpenAPIRoute.delete(
    vararg modules: RouteOpenAPIModule,
    example: TResponse? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams) -> Unit
) = route(HttpMethod.Delete, modules, example, body)

/**
 * Builds a route to match `HEAD` requests generating OpenAPI documentation.
 * Route parameters will have the type [TParams] and response type will have [TResponse].
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param example optional example of [TResponse] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any> NormalOpenAPIRoute.head(
    vararg modules: RouteOpenAPIModule,
    example: TResponse? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams) -> Unit
) = route(HttpMethod.Head, modules, example, body)

/**
 * Builds a route to match requests with the specified [method] generating OpenAPI documentation.
 * Route parameters will have the type [TParams] and response type will have [TResponse].
 * The body of the request will be parsed into a [TRequest] instance.
 * Any of the template types can be specified as [Unit] if they are not used.
 *
 * @param method the HTTP method that matches this route
 * @param modules to add OpenAPI details. See [com.papsign.ktor.openapigen.route.info], [com.papsign.ktor.openapigen.route.status], [com.papsign.ktor.openapigen.route.tags] or any other implementation of module
 * @param exampleResponse optional example of [TResponse] to add to OpenAPI specification
 * @param exampleRequest optional example of [TRequest] to add to OpenAPI specification
 * @param body a block that received the request parameters builds the response
 * @return the new created route
 */
@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any> NormalOpenAPIRoute.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams, TRequest) -> Unit
) {
    method(method).apply { modules.forEach { provider.registerModule(it, it::class.starProjectedType) } }
        .handle(exampleResponse, exampleRequest, body)
}

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any> NormalOpenAPIRoute.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exampleResponse: TResponse? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams) -> Unit
) {
    method(method).apply { modules.forEach { provider.registerModule(it, it::class.starProjectedType) } }
        .handle(exampleResponse, body)
}

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any> NormalOpenAPIRoute.handle(
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams, TRequest) -> Unit
) {
    val paramsType = typeOf<TParams>()
    val responseType = typeOf<TResponse>()
    val requestType = typeOf<TRequest>()
    preHandle<TParams, TResponse, TRequest, NormalOpenAPIRoute>(exampleResponse, exampleRequest) {
        handle(paramsType, responseType, requestType, body)
    }
}

@ContextDsl
inline fun <reified TParams : Any, reified TResponse : Any> NormalOpenAPIRoute.handle(
    exampleResponse: TResponse? = null,
    noinline body: suspend OpenAPIPipelineResponseContext<TResponse>.(TParams) -> Unit
) {
    val paramsType = typeOf<TParams>()
    val responseType = typeOf<TResponse>()
    preHandle<TParams, TResponse, Unit, NormalOpenAPIRoute>(exampleResponse, Unit) {
        handle(paramsType, responseType, body)
    }
}
