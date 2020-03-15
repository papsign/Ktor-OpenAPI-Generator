package com.papsign.ktor.openapigen.schema.builder.provider

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.schema.builder.FinalSchemaBuilder

interface FinalSchemaBuilderProviderModule: OpenAPIModule {
    fun provide(apiGen: OpenAPIGen, provider: ModuleProvider<*>): FinalSchemaBuilder
}
