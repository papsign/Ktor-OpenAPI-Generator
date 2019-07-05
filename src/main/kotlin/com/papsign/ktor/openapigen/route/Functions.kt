package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.modules.handlers.RequestHandlerModule
import com.papsign.ktor.openapigen.modules.handlers.ResponseHandlerModule
import com.papsign.ktor.openapigen.route.modules.HttpMethodProviderModule
import com.papsign.ktor.openapigen.route.modules.ParamClassProviderModule
import com.papsign.ktor.openapigen.route.modules.PathProviderModule
import io.ktor.http.HttpMethod
import io.ktor.routing.HttpMethodRouteSelector
import io.ktor.routing.createRouteFromPath
import io.ktor.util.pipeline.ContextDsl
import kotlin.reflect.full.findAnnotation

fun <T: OpenAPIRoute<T>> T.route(path: String): T {
    return child(ktorRoute.createRouteFromPath(path)).apply {
        provider.registerModule(PathProviderModule(path))
    }
}

@ContextDsl
inline fun <T: OpenAPIRoute<T>> T.route(path: String, crossinline fn: T.() -> Unit) {
    route(path).fn()
}

fun <T: OpenAPIRoute<T>> T.method(method: HttpMethod): T {
    return child(ktorRoute.createChild(HttpMethodRouteSelector(method))).apply {
        provider.registerModule(HttpMethodProviderModule(method))
    }
}

@ContextDsl
inline fun <T: OpenAPIRoute<T>> T.method(method: HttpMethod, crossinline fn: T.() -> Unit) {
    method(method).fn()
}

fun <T: OpenAPIRoute<T>> T.provider(vararg content: ContentTypeProvider): T {
    return child().apply {
        content.forEach {
            provider.registerModule(it)
        }
    }
}

@ContextDsl
inline fun <T: OpenAPIRoute<T>> T.provider(vararg content: ContentTypeProvider, crossinline fn: T.() -> Unit) {
    provider(*content).fn()
}


fun <T: OpenAPIRoute<T>> T.tag(tag: APITag): T {
    return child().apply {
        provider.registerModule(TagModule(listOf(tag)))
    }
}


@ContextDsl
inline fun <T: OpenAPIRoute<T>> T.tag(tag: APITag, crossinline fn: T.() -> Unit) {
    tag(tag).fn()
}

inline fun <reified P : Any, reified R : Any, reified B : Any, T: OpenAPIRoute<T>> T.preHandle(
    exampleResponse: R? = null,
    exampleRequest: B? = null,
    handle: T.() -> Unit
) {
    val path = P::class.findAnnotation<Path>()
    val new = if (path != null) child(ktorRoute.createRouteFromPath(path.path)) else child()
    new.apply {
        provider.registerModule(
            RequestHandlerModule.create(
                exampleRequest
            )
        )
        provider.registerModule(
            ResponseHandlerModule.create(
                exampleResponse
            )
        )
        if (path != null) {
            provider.registerModule(PathProviderModule(path.path))
        }
        if (Unit !is P)
            provider.registerModule(ParamClassProviderModule(P::class))
        handle()
    }
}