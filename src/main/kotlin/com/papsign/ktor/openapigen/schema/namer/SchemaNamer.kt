package com.papsign.ktor.openapigen.schema.namer

import com.papsign.ktor.openapigen.deepStrip
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import kotlin.reflect.KType

interface SchemaNamer: OpenAPIModule {
    operator fun get(type: KType): String

    object Default: SchemaNamer {
        override fun get(type: KType): String {
            return type.deepStrip().toString()
        }
    }
}
