package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.content.type.SelectedModule
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.HandlerModule
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.MethodProvider
import com.papsign.ktor.openapigen.modules.providers.PathProvider
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.PathItem
import com.papsign.ktor.openapigen.parameters.parsers.NoTranslation
import com.papsign.ktor.openapigen.parameters.parsers.OpenAPIPathSegmentTranslator
import com.papsign.ktor.openapigen.parameters.parsers.ParameterHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object RouteHandler: HandlerModule {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>) {
        val methods = provider.ofClass<MethodProvider>()
        if (methods.size > 1) error("API cannot have two methods simultaneously: ${methods.map { it.method.value }}")
        val paths = provider.ofClass<PathProvider>()
        val translator = getTranslator(provider)
        val path = "/${paths.flatMap { it.path.split('/').filter(String::isNotEmpty).map(translator::translateSegment) }.joinToString("/")}"
        val operationModules = provider.ofClass<OperationModule>()
        apiGen.api.paths.getOrPut(path) { PathItem() }.also {pathItem ->
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

    private fun getTranslator(provider: ModuleProvider<*>): OpenAPIPathSegmentTranslator {
        val translator = provider.ofClass<OpenAPIPathSegmentTranslator>()
        if (translator.size > 1) log.warn("Too many Path Segment Translators, choosing first: $translator")
        if (translator.isEmpty()) log.debug("No Path Segment Translator, choosing default")
        return translator.firstOrNull() ?: NoTranslation
    }
}
