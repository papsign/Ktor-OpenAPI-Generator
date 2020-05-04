package com.papsign.ktor.openapigen.schema.namer

import kotlin.reflect.KType

object DefaultSchemaNamer : SchemaNamer {
    override fun get(type: KType): String = type.toString()
}
