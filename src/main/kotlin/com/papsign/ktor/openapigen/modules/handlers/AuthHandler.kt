package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.model.security.SecurityModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.AuthProvider

object AuthHandler: OperationModule {

    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: OperationModel) {
        val authHandlers = provider.ofClass<AuthProvider<*>>()
        val security = authHandlers.flatMap { it.security }.distinct()
        operation.security = security.map { SecurityModel().also { sec ->
            it.forEach { sec[it.scheme.name] = it.requirements }
        } }
        apiGen.api.components.securitySchemes.putAll(security.flatMap { it.map { it.scheme } }.associateBy { it.name })
    }

}
