package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.modules.DependentModule
import com.papsign.ktor.openapigen.modules.DependentModule.Companion.handler
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.handlers.AuthHandler
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KType

interface AuthProvider<TAuth>: OpenAPIModule, DependentModule {
    suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): TAuth
    fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<TAuth>
    val security: Iterable<Iterable<Security<*>>>
    override val handlers: Collection<Pair<KType, OpenAPIModule>>
        get() = listOf(handler(AuthHandler))

    data class  Security<TScope>(val scheme: SecuritySchemeModel<TScope>, val requirements: List<TScope>) where TScope: Enum<TScope>, TScope: Described
}
