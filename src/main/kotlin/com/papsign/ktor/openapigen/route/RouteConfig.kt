package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.ktor.application.Application
import io.ktor.application.feature
import io.ktor.routing.Routing
import io.ktor.routing.routing
import io.ktor.util.pipeline.ContextDsl

@ContextDsl
fun Application.apiRouting(config: NormalOpenAPIRoute.() -> Unit) {
    routing {
        NormalOpenAPIRoute(
                this,
                application.feature(OpenAPIGen).globalModuleProvider
        ).apply(config)
    }
}

@ContextDsl
fun Routing.apiRouting(config: NormalOpenAPIRoute.() -> Unit) {
    NormalOpenAPIRoute(
            this,
            application.feature(OpenAPIGen).globalModuleProvider
    ).apply(config)
}

