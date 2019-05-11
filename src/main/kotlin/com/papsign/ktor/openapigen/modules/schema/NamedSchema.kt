package com.papsign.ktor.openapigen.modules.schema

import com.papsign.ktor.openapigen.openapi.Schema

data class NamedSchema(val name: String, val schema: Schema<*>)