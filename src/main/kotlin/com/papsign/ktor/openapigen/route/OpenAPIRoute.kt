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
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.parameters.handlers.ParameterHandler
import com.papsign.ktor.openapigen.parameters.util.buildParameterHandler
import com.papsign.ktor.openapigen.route.response.Responder
import com.papsign.ktor.openapigen.validation.ValidationHandler
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance
import kotlin.reflect.full.createType

abstract class OpenAPIRoute<T : OpenAPIRoute<T>>(val ktorRoute: Route, val provider: CachingModuleProvider) {
    private val log = classLogger()

    abstract fun child(route: Route = this.ktorRoute): T

    fun <P : Any, R : Any, B : Any> handle(
        paramsType: KType,
        responseType: KType,
        bodyType: KType,
        pass: suspend OpenAPIRoute<*>.(pipeline: PipelineContext<Unit, ApplicationCall>, responder: Responder, P, B) -> Unit
    ) {
        val parameterHandler = buildParameterHandler<P>(paramsType)
        provider.registerModule(parameterHandler, ParameterHandler::class.createType(listOf(KTypeProjection(KVariance.INVARIANT, paramsType))))

        val apiGen = ktorRoute.application.openAPIGen
        provider.ofType<HandlerModule>().forEach {
            it.configure(apiGen, provider)
        }

        val BHandler = ValidationHandler.build(bodyType)
        val PHandler = ValidationHandler.build(paramsType)

        ktorRoute.apply {
            getAcceptMap<R>(responseType).let {
                it.ifEmpty { listOf(ContentType.Any to listOf(SelectedSerializer(KtorContentProvider))) }
            }.forEach { (acceptType, serializers) ->
                val responder = ContentTypeResponder(serializers.getResponseSerializer(acceptType), acceptType)
                accept(acceptType) {
                    if (bodyType.classifier == Unit::class) {
                        handle {
                            @Suppress("UNCHECKED_CAST")
                            val params: P = if (paramsType.classifier == Unit::class) Unit as P else parameterHandler.parse(call.parameters, call.request.headers)
                            @Suppress("UNCHECKED_CAST")
                            pass(this, responder, PHandler.handle(params), Unit as B)
                        }
                    } else {
                        getContentTypesMap<B>(bodyType).forEach { (contentType, parsers) ->
                            contentType(contentType) {
                                handle {
                                    val receive: B = parsers.getBodyParser(call.request.contentType()).parseBody(bodyType, this)
                                    @Suppress("UNCHECKED_CAST")
                                    val params: P = if (paramsType.classifier == Unit::class) Unit as P else parameterHandler.parse(call.parameters, call.request.headers)
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

    fun <B : Any> getContentTypesMap(type: KType) = mapContentTypes<SelectedParser> { module.getParseableContentTypes<B>(type) }

    fun <R : Any> getAcceptMap(type: KType) = mapContentTypes<SelectedSerializer> { module.getSerializableContentTypes<R>(type) }

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
