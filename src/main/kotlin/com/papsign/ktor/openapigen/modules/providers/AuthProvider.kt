package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.modules.DependentModule
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.handlers.AuthHandler
import com.papsign.ktor.openapigen.openapi.Described
import com.papsign.ktor.openapigen.openapi.SecurityScheme
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

interface AuthProvider<A>: OpenAPIModule, DependentModule {
    suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): A
    fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<A>
    val security: Iterable<Iterable<Security<*>>>
    override val handlers: Collection<OpenAPIModule>
        get() = listOf(AuthHandler)

    data class  Security<T>(val scheme: SecurityScheme<T>, val requirements: List<T>) where T: Enum<T>, T: Described
}