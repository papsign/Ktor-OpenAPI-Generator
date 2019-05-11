package com.papsign.ktor.openapigen.modules.schema

import kotlin.reflect.KType

interface PartialSchemaRegistrar {
    operator fun get(type: KType): NamedSchema?
}