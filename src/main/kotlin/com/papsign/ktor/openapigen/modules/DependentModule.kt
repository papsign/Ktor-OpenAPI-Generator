package com.papsign.ktor.openapigen.modules

import com.papsign.ktor.openapigen.getKType
import kotlin.reflect.KType

interface DependentModule {
    val handlers: Collection<Pair<KType, OpenAPIModule>>

    companion object {
        inline fun <reified T: OpenAPIModule> handler(handler: T): Pair<KType, T> {
            return getKType<T>() to handler
        }
    }
}
