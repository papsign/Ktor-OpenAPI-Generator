package com.papsign.ktor.openapigen.schema.builder

import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.builder.SchemaBuilder
import kotlin.reflect.KType

interface FinalSchemaBuilder: SchemaBuilder {
    fun build(type: KType): SchemaModel<*>
}
