package com.papsign.ktor.openapigen.route

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.content.type.*
import com.papsign.ktor.openapigen.content.type.ktor.KtorContentProvider
import com.papsign.ktor.openapigen.exceptions.OpenAPINoParserException
import com.papsign.ktor.openapigen.exceptions.OpenAPINoSerializerException
import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.HandlerModule
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.response.Responder
import com.papsign.ktor.openapigen.validators.ValidationHandler
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

abstract class OpenAPIRoute<T : OpenAPIRoute<T>>(val ktorRoute: Route, val provider: CachingModuleProvider) {
    private val log = classLogger()

    abstract fun child(route: Route = this.ktorRoute): T

    inline fun <reified P : Any, reified R : Any, reified B : Any> handle(
            crossinline pass: suspend OpenAPIRoute<*>.(pipeline: PipelineContext<Unit, ApplicationCall>, responder: Responder, P, B) -> Unit
    ) {
        val apiGen = ktorRoute.application.openAPIGen
        provider.ofClass<HandlerModule>().forEach {
            it.configure(apiGen, provider)
        }

        val BHandler = ValidationHandler<B>(provider)
        val PHandler = ValidationHandler<P>(provider)

        ktorRoute.apply {
            getAcceptMap(R::class).let {
                if (it.isNotEmpty()) it else listOf(ContentType.Any to listOf(SelectedSerializer(KtorContentProvider)))
            }.forEach { (acceptType, serializers) ->
                val responder = ContentTypeResponder(serializers.getResponseSerializer(acceptType), acceptType)
                accept(acceptType) {
                    if (Unit is B) {
                        handle {
                            val params: P = if (Unit is P) Unit else buildParameterObject(call, P::class.java)
                            pass(this, responder, PHandler.handle(params), Unit)
                        }
                    } else {
                        getContentTypesMap(B::class).forEach { (contentType, parsers) ->
                            contentType(contentType) {
                                handle {
                                    val receive: B = parsers.getBodyParser(call.request.contentType()).parseBody(B::class, this)
                                    val params: P = if (Unit is P) Unit else buildParameterObject(call, P::class.java)
                                    pass(this, responder, PHandler.handle(params), BHandler.handle(receive))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun <T> buildParameterObject(call: ApplicationCall, clazz: Class<T>): T {
            return mapper.convertValue(call.parameters.names().associateWith { call.parameters[it] }, clazz)
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
        return provider.ofClass<T>().flatMap { parser ->
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
