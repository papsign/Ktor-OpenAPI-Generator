package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.modules.DependentModule
import com.papsign.ktor.openapigen.modules.DependentModule.Companion.handler
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.handlers.RouteHandler
import io.ktor.http.HttpMethod
import kotlin.reflect.KType

interface MethodProvider : OpenAPIModule, DependentModule {
    val method: HttpMethod
    override val handlers: Collection<Pair<KType, OpenAPIModule>>
        get() = listOf(handler(RouteHandler))
}
