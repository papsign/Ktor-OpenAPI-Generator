package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.modules.DependentModule
import com.papsign.ktor.openapigen.modules.DependentModule.Companion.handler
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.modules.handlers.TagHandlerModule
import kotlin.reflect.KType

interface TagProviderModule: RouteOpenAPIModule, DependentModule {
    val tags: Collection<APITag>
    override val handlers: Collection<Pair<KType, OpenAPIModule>>
        get() = listOf(handler(TagHandlerModule))
}
