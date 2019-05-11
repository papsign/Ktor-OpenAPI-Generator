package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.Security

object AuthHandler: OperationModule {

    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {
        val authHandlers = provider.ofClass<AuthProvider<*>>()
        val security = authHandlers.flatMap { it.security }.distinct()
        operation.security = security.map { Security().also {sec ->
            it.forEach { sec[it.scheme.name] = it.requirements }
        } }
        apiGen.api.components.securitySchemes.putAll(security.flatMap { it.map { it.scheme } }.associateBy { it.name })
    }

}