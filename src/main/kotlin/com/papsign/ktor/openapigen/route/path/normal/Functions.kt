package com.papsign.ktor.openapigen.route.path.normal

import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.route.method
import com.papsign.ktor.openapigen.route.preHandle
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import io.ktor.http.HttpMethod
import io.ktor.util.pipeline.ContextDsl
import kotlin.reflect.full.starProjectedType


@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.get(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) = route(HttpMethod.Get, modules, example, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.post(
    vararg modules: RouteOpenAPIModule,
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) = route(HttpMethod.Post, modules, exampleResponse, exampleRequest, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.put(
    vararg modules: RouteOpenAPIModule,
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) = route(HttpMethod.Put, modules, exampleResponse, exampleRequest, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.patch(
    vararg modules: RouteOpenAPIModule,
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) = route(HttpMethod.Patch, modules, exampleResponse, exampleRequest, body)

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
    method(method).apply { modules.forEach { provider.registerModule(it, it::class.starProjectedType) } }.handle(exampleResponse, exampleRequest, body)
}

@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exampleResponse: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) {
    method(method).apply { modules.forEach { provider.registerModule(it, it::class.starProjectedType) } }.handle(exampleResponse, body)
}

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any> NormalOpenAPIRoute.handle(
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
) {
    preHandle<P, R, B, NormalOpenAPIRoute>(exampleResponse, exampleRequest) {
        handle(P::class, R::class, B::class, body)
    }
}

@ContextDsl
inline fun <reified P : Any, reified R : Any> NormalOpenAPIRoute.handle(
    exampleResponse: R? = null,
    crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
) {
    preHandle<P, R, Unit, NormalOpenAPIRoute>(exampleResponse, Unit) {
        handle(P::class, R::class, body)
    }
}
