package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.modules.DependentModule
import com.papsign.ktor.openapigen.modules.DependentModule.Companion.handler
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.handlers.ThrowOperationHandler
import kotlin.reflect.KType

interface ThrowInfoProvider: OpenAPIModule, DependentModule {
    val exceptions: List<APIException<*, *>>
    override val handlers: Collection<Pair<KType, OpenAPIModule>>
        get() = listOf(handler(ThrowOperationHandler))
}
