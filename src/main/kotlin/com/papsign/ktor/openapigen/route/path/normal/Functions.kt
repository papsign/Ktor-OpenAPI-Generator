package com.papsign.ktor.openapigen.route.path.normal

import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.route.preHandle
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import com.papsign.ktor.openapigen.route.method
import io.ktor.http.HttpMethod
import io.ktor.util.pipeline.ContextDsl


@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.get(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) = route(HttpMethod.Get, modules, example, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.post(
    vararg modules: RouteOpenAPIModule,
    exampleRequest: R? = null,
    exampleResponse: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) = route(HttpMethod.Post, modules, exampleRequest, exampleResponse, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.put(
    vararg modules: RouteOpenAPIModule,
    exampleRequest: R? = null,
    exampleResponse: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) = route(HttpMethod.Put, modules, exampleRequest, exampleResponse, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.patch(
    vararg modules: RouteOpenAPIModule,
    exampleRequest: R? = null,
    exampleResponse: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) = route(HttpMethod.Patch, modules, exampleRequest, exampleResponse, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.delete(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) = route(HttpMethod.Delete, modules, example, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.head(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) = route(HttpMethod.Head, modules, example, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) {
    method(method).apply { modules.forEach(provider::registerModule) }.handle(exampleResponse, exampleRequest, body)
}

@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exampleResponse: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) {
    method(method).apply { modules.forEach(provider::registerModule) }.handle(exampleResponse, body)
}

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.handle(
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) {
    preHandle<P, R, B, NormalOpenAPIRoute>(exampleResponse, exampleRequest) {
        handle(body)
    }
}

@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.handle(
    exampleResponse: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) {
    preHandle<P, R, Unit, NormalOpenAPIRoute>(exampleResponse, Unit) {
        handle(body)
    }
}