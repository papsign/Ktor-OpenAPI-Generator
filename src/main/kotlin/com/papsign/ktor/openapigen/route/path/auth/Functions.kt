package com.papsign.ktor.openapigen.route.path.auth

import com.papsign.ktor.openapigen.interop.OAuth2Handler
import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.openapi.Described
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.method
import com.papsign.ktor.openapigen.route.preHandle
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineAuthContext
import io.ktor.http.HttpMethod
import io.ktor.util.pipeline.ContextDsl

@ContextDsl
inline fun <reified P : Any, reified R : Any, A> OpenAPIAuthenticatedRoute<A>.get(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P) -> Unit
) = route<P, R, Unit, A>(HttpMethod.Get, modules, example) { p, _ -> body(p) }

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any, A> OpenAPIAuthenticatedRoute<A>.post(
    vararg modules: RouteOpenAPIModule,
    exampleRequest: R? = null,
    exampleResponse: B? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P, B) -> Unit
) = route(HttpMethod.Post, modules, exampleRequest, exampleResponse, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any, A> OpenAPIAuthenticatedRoute<A>.put(
    vararg modules: RouteOpenAPIModule,
    exampleRequest: R? = null,
    exampleResponse: B? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P, B) -> Unit
) = route(HttpMethod.Put, modules, exampleRequest, exampleResponse, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any, A> OpenAPIAuthenticatedRoute<A>.patch(
    vararg modules: RouteOpenAPIModule,
    exampleRequest: R? = null,
    exampleResponse: B? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P, B) -> Unit
) = route(HttpMethod.Patch, modules, exampleRequest, exampleResponse, body)

@ContextDsl
inline fun <reified P : Any, reified R : Any, A> OpenAPIAuthenticatedRoute<A>.delete(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P) -> Unit
) = route<P, R, Unit, A>(HttpMethod.Delete, modules, example) { p, _ -> body(p) }

@ContextDsl
inline fun <reified P : Any, reified R : Any, A> OpenAPIAuthenticatedRoute<A>.head(
    vararg modules: RouteOpenAPIModule,
    example: R? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P) -> Unit
) = route<P, R, Unit, A>(HttpMethod.Head, modules, example) { p, _ -> body(p) }

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any, A> OpenAPIAuthenticatedRoute<A>.route(
    method: HttpMethod,
    modules: Array<out RouteOpenAPIModule>,
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P, B) -> Unit
) {
    method(method).apply { modules.forEach(provider::registerModule) }.handle(exampleResponse, exampleRequest, body)
}

@ContextDsl
inline fun <reified P : Any, reified R : Any, reified B : Any, A> OpenAPIAuthenticatedRoute<A>.handle(
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P, B) -> Unit
) {
    preHandle<P, R, B, OpenAPIAuthenticatedRoute<A>>(exampleResponse, exampleRequest) {
        handle(body)
    }
}

suspend fun <A> OpenAPIPipelineAuthContext<A, *>.principal() = authProvider.getAuth(pipeline)


inline fun <A, T> NormalOpenAPIRoute.auth(handler: OAuth2Handler<A, T>, vararg scopes: T, noinline fn: OpenAPIAuthenticatedRoute<A>.()->Unit) where T: Enum<T>, T: Described = auth(handler, scopes.asList(), fn)

inline fun <A, T> NormalOpenAPIRoute.auth(handler: OAuth2Handler<A, T>, scopes: List<T>, noinline fn: OpenAPIAuthenticatedRoute<A>.()->Unit) where T: Enum<T>, T: Described {
    handler.auth(this, scopes).fn()
}
