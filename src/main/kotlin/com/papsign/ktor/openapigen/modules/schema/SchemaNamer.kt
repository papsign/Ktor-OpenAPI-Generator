package com.papsign.ktor.openapigen.modules.schema

import kotlin.reflect.KType

interface SchemaNamer {
    operator fun get(type: KType): String
}