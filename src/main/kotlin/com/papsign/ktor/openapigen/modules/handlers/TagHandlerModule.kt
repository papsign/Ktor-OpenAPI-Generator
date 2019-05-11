package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.modules.providers.TagProviderModule
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.openapi.Operation

object TagHandlerModule: OperationModule {
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {
        val tags = provider.ofClass<TagProviderModule>().flatMap { it.tags.map(apiGen::getOrRegisterTag) }
        val current = operation.tags
        if (current != null) {
            operation.tags = (tags + current).distinct()
        } else {
            operation.tags = tags
        }
    }

}