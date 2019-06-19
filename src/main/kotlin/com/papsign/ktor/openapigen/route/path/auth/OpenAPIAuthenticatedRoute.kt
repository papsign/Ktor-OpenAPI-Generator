package com.papsign.ktor.openapigen.route.path.auth

import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.route.OpenAPIRoute
import com.papsign.ktor.openapigen.route.response.AuthResponseContextImpl
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineAuthContext
import io.ktor.routing.Route

class OpenAPIAuthenticatedRoute<A>(route: Route, provider: CachingModuleProvider = CachingModuleProvider(), val authProvider: AuthProvider<A>): OpenAPIRoute<OpenAPIAuthenticatedRoute<A>>(route, provider) {

    override fun child(route: Route): OpenAPIAuthenticatedRoute<A> {
        return OpenAPIAuthenticatedRoute(route, provider.child(), authProvider)
    }

    inline fun <reified P : Any, reified R: Any, reified B : Any> handle(crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P, B) -> Unit) {
        child().apply {// child in case path is branch to prevent propagation of the mutable nature of the provider
            provider.registerModule(authProvider)
            handle<P, R, B> { pipeline, responder, p, b ->
                AuthResponseContextImpl<A, R>(pipeline, authProvider, this, responder).body(p, b)
            }
        }
    }

    inline fun <reified P : Any, reified R: Any> handle(crossinline body: suspend OpenAPIPipelineAuthContext<A, R>.(P) -> Unit) {
        child().apply {// child in case path is branch to prevent propagation of the mutable nature of the provider
            provider.registerModule(authProvider)
            handle<P, R, Unit> { pipeline, responder, p: P, _ ->
                AuthResponseContextImpl<A, R>(pipeline, authProvider, this, responder).body(p)
            }
        }
    }
}