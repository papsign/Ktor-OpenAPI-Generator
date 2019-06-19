package com.papsign.ktor.openapigen.route.path.normal

import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.route.OpenAPIRoute
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineResponseContext
import com.papsign.ktor.openapigen.route.response.ResponseContextImpl
import io.ktor.routing.Route

class NormalOpenAPIRoute(route: Route, provider: CachingModuleProvider = CachingModuleProvider()): OpenAPIRoute<NormalOpenAPIRoute>(route, provider) {

    override fun child(route: Route): NormalOpenAPIRoute {
        return NormalOpenAPIRoute(route, provider.child())
    }

    inline fun <reified P : Any, reified R: Any, reified B : Any> handle(crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P, B) -> Unit) {
        handle<P, R, B> {pipeline, responder, p, b ->
            ResponseContextImpl<R>(pipeline, this, responder).body(p, b)
        }
    }

    inline fun <reified P : Any, reified R: Any> handle(crossinline body: suspend OpenAPIPipelineResponseContext<R>.(P) -> Unit) {
        handle<P, R, Unit> {pipeline, responder, p, _ ->
            ResponseContextImpl<R>(pipeline, this, responder).body(p)
        }
    }
}
