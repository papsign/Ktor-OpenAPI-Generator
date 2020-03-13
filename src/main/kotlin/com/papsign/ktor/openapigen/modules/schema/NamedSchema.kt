package com.papsign.ktor.openapigen.modules.schema

import com.papsign.ktor.openapigen.model.schema.SchemaModel


data class NamedSchema(val name: String, val schema: SchemaModel<*>)
