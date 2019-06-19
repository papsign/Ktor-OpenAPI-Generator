package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.SelectedModule
import com.papsign.ktor.openapigen.content.type.SelectedParser
import com.papsign.ktor.openapigen.content.type.SelectedSerializer
import com.papsign.ktor.openapigen.generator.ParamBuilder
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.HandlerModule
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.MethodProvider
import com.papsign.ktor.openapigen.modules.providers.ParameterProvider
import com.papsign.ktor.openapigen.modules.providers.PathProvider
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.PathItem
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object RouteHandler: HandlerModule {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>) {
        val methods = provider.ofClass<MethodProvider>()
        if (methods.size > 1) error("API cannot have two methods simultaneously: ${methods.map { it.method.value }}")
        val paths = provider.ofClass<PathProvider>()
        val path = "/${paths.map { it.path.trim('/') }.filter { it.isNotBlank() }.joinToString("/")}"
        val parameters = provider.ofClass<ParameterProvider>().flatMap { it.getParameters(ParamBuilder(apiGen, provider)) }
        val operationModules = provider.ofClass<OperationModule>()
        apiGen.api.paths.getOrPut(path) { PathItem() }.also {pathItem ->
            pathItem.parameters = (pathItem.parameters + parameters).distinct()
            methods.forEach {
                val name = it.method.value.toLowerCase()
                //if (pathItem.containsKey(name)) error("$path::$name already defined")
                val op = pathItem.getOrPut(name) { Operation() } as Operation
                operationModules.forEach {
                    it.configure(apiGen, provider, op)
                }
            }
        }
        log.trace("Registered $path::${methods.map { it.method.value }} with OpenAPI description with ${provider.ofClass<SelectedModule>().map { it.module::class.simpleName }}")
    }
}