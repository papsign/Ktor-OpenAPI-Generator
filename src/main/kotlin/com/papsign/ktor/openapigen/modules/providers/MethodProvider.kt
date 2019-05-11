package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.modules.DependentModule
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.handlers.RouteHandler
import io.ktor.http.HttpMethod

interface MethodProvider : OpenAPIModule, DependentModule {
    val method: HttpMethod
    override val handlers: Collection<OpenAPIModule>
        get() = listOf(RouteHandler)
}