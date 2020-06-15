package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.content.type.SelectedModule
import com.papsign.ktor.openapigen.model.base.PathItemModel
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofType
import com.papsign.ktor.openapigen.modules.openapi.HandlerModule
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.MethodProvider
import com.papsign.ktor.openapigen.modules.providers.PathProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object RouteHandler: HandlerModule {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>) {
        val methods = provider.ofType<MethodProvider>()
        if (methods.size > 1) error("API cannot have two methods simultaneously: ${methods.map { it.method.value }}")
        val paths = provider.ofType<PathProvider>()
        val path = "/${paths.flatMap { it.path.split('/').filter(String::isNotEmpty) }.joinToString("/")}"
        val operationModules = provider.ofType<OperationModule>()
        apiGen.api.paths.getOrPut(path) { PathItemModel() }.also {pathItem ->
            methods.forEach {
                val name = it.method.value.toLowerCase()
                //if (pathItem.containsKey(name)) error("$path::$name already defined")
                val op = pathItem.getOrPut(name) { OperationModel() } as OperationModel
                operationModules.forEach {
                    it.configure(apiGen, provider, op)
                }
            }
        }
        log.trace("Registered $path::${methods.map { it.method.value }} with OpenAPI description with ${provider.ofType<SelectedModule>().map { it.module::class.simpleName }}")
    }
}
