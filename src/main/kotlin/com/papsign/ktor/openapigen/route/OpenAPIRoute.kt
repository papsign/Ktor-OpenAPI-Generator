package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.content.type.*
import com.papsign.ktor.openapigen.content.type.ktor.KtorContentProvider
import com.papsign.ktor.openapigen.exceptions.OpenAPINoParserException
import com.papsign.ktor.openapigen.exceptions.OpenAPINoSerializerException
import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.ofType
import com.papsign.ktor.openapigen.modules.openapi.HandlerModule
import com.papsign.ktor.openapigen.modules.registerModule
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.parameters.util.buildParameterHandler
import com.papsign.ktor.openapigen.route.response.Responder
import com.papsign.ktor.openapigen.validation.ValidationHandler
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.contentType
import io.ktor.routing.Route
import io.ktor.routing.accept
import io.ktor.routing.application
import io.ktor.routing.contentType
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf

abstract class OpenAPIRoute<T : OpenAPIRoute<T>>(val ktorRoute: Route, val provider: CachingModuleProvider) {
    private val log = classLogger()

    abstract fun child(route: Route = this.ktorRoute): T

    inline fun <P : Any, R : Any, B : Any> handle(
            paramsClass: KClass<P>,
            responseClass: KClass<R>,
            bodyClass: KClass<B>,
            crossinline pass: suspend OpenAPIRoute<*>.(pipeline: PipelineContext<Unit, ApplicationCall>, responder: Responder, P, B) -> Unit
    ) {
        val parameterHandler = buildParameterHandler<P>(paramsClass)
        provider.registerModule(parameterHandler)

        val apiGen = ktorRoute.application.openAPIGen
        provider.ofType<HandlerModule>().forEach {
            it.configure(apiGen, provider)
        }

        val BHandler = ValidationHandler.build(bodyClass)
        val PHandler = ValidationHandler.build(paramsClass)

        ktorRoute.apply {
            getAcceptMap(responseClass).let {
                if (it.isNotEmpty()) it else listOf(ContentType.Any to listOf(SelectedSerializer(KtorContentProvider)))
            }.forEach { (acceptType, serializers) ->
                val responder = ContentTypeResponder(serializers.getResponseSerializer(acceptType), acceptType)
                accept(acceptType) {
                    if (bodyClass == Unit::class) {
                        handle {
                            @Suppress("UNCHECKED_CAST")
                            val params: P = if (paramsClass == Unit::class) Unit as P else parameterHandler.parse(call.parameters, call.request.headers)
                            @Suppress("UNCHECKED_CAST")
                            pass(this, responder, PHandler.handle(params), Unit as B)
                        }
                    } else {
                        if(paramsClass == Unit::class)
                        getContentTypesMap(bodyClass).forEach { (contentType, parsers) ->
                            contentType(contentType) {
                                handle {
                                    val receive: B = parsers.getBodyParser(call.request.contentType()).parseBody(bodyClass.starProjectedType, this)
                                    @Suppress("UNCHECKED_CAST")
                                    val params: P = if (paramsClass == Unit::class) Unit as P else parameterHandler.parse(call.parameters, call.request.headers)
                                    pass(this, responder, PHandler.handle(params), BHandler.handle(receive))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun List<SelectedSerializer>.getResponseSerializer(contentType: ContentType): ResponseSerializer {
        if (size > 1) log.warn("Multiple equal serializers for Accept $contentType: ${map { it.module::class.simpleName }}, selecting first ${first().module::class.simpleName}")
        return firstOrNull()?.module ?: throw OpenAPINoSerializerException(contentType)
    }

    fun List<SelectedParser>.getBodyParser(contentType: ContentType): BodyParser {
        if (size > 1) log.warn("Multiple equal parsers for Content-Type $contentType: ${map { it.module::class.simpleName }}, selecting first ${first().module::class.simpleName}")
        return firstOrNull()?.module ?: throw OpenAPINoParserException(contentType)
    }

    fun <B : Any> getContentTypesMap(clazz: KClass<B>) = mapContentTypes<SelectedParser> { module.getParseableContentTypes(clazz) }

    fun <R : Any> getAcceptMap(clazz: KClass<R>) = mapContentTypes<SelectedSerializer> { module.getSerializableContentTypes(clazz) }

    inline fun <reified T : OpenAPIModule> mapContentTypes(noinline fn: T.() -> List<ContentType>): List<Pair<ContentType, List<T>>> {
        return provider.ofType<T>().flatMap { parser ->
            parser.fn().map { Pair(it, parser) }
        }.groupBy { it.first }.mapValues { it.value.map { it.second } }.map { Pair(it.key, it.value) }.sortedBy {
            val ct = it.first
            when {
                ct.contentSubtype != "*" -> 1000
                ct.contentType != "*" -> 10000
                else -> 100000
            } - ct.parameters.size // edge case already, no need to sort by potential wildcards too, if you do this you are already looking for problems
        }
    }
}
