package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.modules.handlers.RequestHandlerModule
import com.papsign.ktor.openapigen.modules.handlers.ResponseHandlerModule
import com.papsign.ktor.openapigen.modules.registerModule
import com.papsign.ktor.openapigen.route.modules.HttpMethodProviderModule
import com.papsign.ktor.openapigen.route.modules.PathProviderModule
import io.ktor.http.HttpMethod
import io.ktor.routing.HttpMethodRouteSelector
import io.ktor.routing.createRouteFromPath
import io.ktor.util.pipeline.ContextDsl
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

fun <T : OpenAPIRoute<T>> T.route(path: String): T {
    return child(ktorRoute.createRouteFromPath(path)).apply {
        provider.registerModule(PathProviderModule(path))
    }
}

/**
 * Creates a new route matching the specified [path]
 */
@ContextDsl
inline fun <TRoute : OpenAPIRoute<TRoute>> TRoute.route(path: String, crossinline fn: TRoute.() -> Unit) {
    route(path).fn()
}

fun <TRoute : OpenAPIRoute<TRoute>> TRoute.method(method: HttpMethod): TRoute {
    return child(ktorRoute.createChild(HttpMethodRouteSelector(method))).apply {
        provider.registerModule(HttpMethodProviderModule(method))
    }
}

/**
 * Creates a new route matching the specified [method]
 */
@ContextDsl
inline fun <TRoute : OpenAPIRoute<TRoute>> TRoute.method(method: HttpMethod, crossinline fn: TRoute.() -> Unit) {
    method(method).fn()
}

fun <TRoute : OpenAPIRoute<TRoute>> TRoute.provider(vararg content: ContentTypeProvider): TRoute {
    return child().apply {
        content.forEach {
            provider.registerModule(it)
        }
    }
}

/**
 * Creates a new route matching the specified [content]
 */
@ContextDsl
inline fun <TRoute : OpenAPIRoute<TRoute>> TRoute.provider(vararg content: ContentTypeProvider, crossinline fn: TRoute.() -> Unit) {
    provider(*content).fn()
}

/**
 * Applies a tag to all children of this route.
 * Parameter [tag] should be an enum that inherits from [APITag], check [APITag] description for
 * an explanation.
 *
 * @param tag the tag to apply
 * @return the same route that received the call to chain multiple calls
 */
fun <TRoute : OpenAPIRoute<TRoute>> TRoute.tag(tag: APITag): TRoute {
    return child().apply {
        provider.registerModule(TagModule(listOf(tag)))
    }
}


/**
 * This method assigns an OpenAPI [tag] too all child routes defined inside [fn].
 * Parameter [tag] should be an enum that inherits from [APITag], check [APITag] description for an
 * explanation.
 *
 * Usage example:
 *
 *  // Defined tags
 *  enum class Tags(override val description: String) : APITag {
 *      EXAMPLE("Wow this is a tag?!")
 *  }
 *
 *  ...
 *  apiRouting {
 *      route("examples") {
 *          tag(Tags.EXAMPLE) { // <-- Applies the tag here
 *              route("getTextData").get<StringParam, StringResponse> { params ->
 *                  respond(StringResponse(params.a))
 *              }
 *              // Multiple routes can be specified here
 *          }
 *      }
 *  }
 *  ...
 *
 * @param tag the tag to apply
 * @param fn the block where the sub routes are defined
 */
@ContextDsl
inline fun <TRoute : OpenAPIRoute<TRoute>> TRoute.tag(tag: APITag, crossinline fn: TRoute.() -> Unit) {
    tag(tag).fn()
}

inline fun <reified TParams : Any, reified TResponse : Any, reified TRequest : Any, TRoute : OpenAPIRoute<TRoute>> TRoute.preHandle(
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    noinline handle: TRoute.() -> Unit
) {
    preHandle<TParams, TResponse, TRequest, TRoute>(
        typeOf<TParams>(),
        typeOf<TResponse>(),
        typeOf<TRequest>(),
        exampleResponse,
        exampleRequest,
        handle
    )
}

// hide this function from public api as it can be "misused" easily but make it accessible to inlined functions from this package
@PublishedApi
internal fun <TParams : Any, TResponse : Any, TRequest : Any, TRoute : OpenAPIRoute<TRoute>> TRoute.preHandle(
    pType: KType,
    rType: KType,
    bType: KType,
    exampleResponse: TResponse? = null,
    exampleRequest: TRequest? = null,
    handle: TRoute.() -> Unit
) {
    val path = pType.jvmErasure.findAnnotation<Path>()
    val new = if (path != null) child(ktorRoute.createRouteFromPath(path.path)) else child()
    new.apply {
        provider.registerModule(
            RequestHandlerModule.create(
                bType,
                exampleRequest
            ),
            RequestHandlerModule::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, bType)))
        )
        provider.registerModule(
            ResponseHandlerModule.create(
                rType,
                exampleResponse
            ),
            ResponseHandlerModule::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, rType)))
        )
        if (path != null) {
            provider.registerModule(PathProviderModule(path.path))
        }
        handle()
    }
}
