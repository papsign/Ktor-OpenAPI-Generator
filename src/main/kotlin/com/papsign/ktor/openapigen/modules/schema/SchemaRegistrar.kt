package com.papsign.ktor.openapigen.modules.schema

import kotlin.reflect.KType

interface SchemaRegistrar {
    operator fun get(type: KType, master: SchemaRegistrar = this): NamedSchema
}