package com.papsign.ktor.openapigen.route.path.normal

import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.route.OpenAPIRoute
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import com.papsign.ktor.openapigen.route.response.ResponseContextImpl
import io.ktor.routing.Route
import kotlin.reflect.KClass

class NormalOpenAPIRoute(route: Route, provider: CachingModuleProvider = CachingModuleProvider()) :
    OpenAPIRoute<NormalOpenAPIRoute>(route, provider) {

    override fun child(route: Route): NormalOpenAPIRoute {
        return NormalOpenAPIRoute(route, provider.child())
    }

    inline fun <P : Any, R : Any, B : Any> handle(
        pClass: KClass<P>,
        rClass: KClass<R>,
        bClass: KClass<B>,
        crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit
    ) {
        handle<P, R, B>(pClass, rClass, bClass) { pipeline, responder, p, b ->
            ResponseContextImpl<R>(pipeline, this, responder).body(p, b)
        }
    }

    inline fun <P : Any, R : Any> handle(
        pClass: KClass<P>,
        rClass: KClass<R>,
        crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit
    ) {
        handle<P, R, Unit>(pClass, rClass, Unit::class) { pipeline, responder, p, _ ->
            ResponseContextImpl<R>(pipeline, this, responder).body(p)
        }
    }
}
