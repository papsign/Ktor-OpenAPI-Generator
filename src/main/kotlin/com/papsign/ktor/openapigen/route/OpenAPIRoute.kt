package com.papsign.ktor.openapigen.route

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.content.type.SelectedParser
import com.papsign.ktor.openapigen.content.type.SelectedSerializer
import com.papsign.ktor.openapigen.exceptions.OpenAPINoParserException
import com.papsign.ktor.openapigen.exceptions.OpenAPINoSerializerException
import com.papsign.ktor.openapigen.modules.CachingModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.HandlerModule
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.response.OpenAPIPipelineContext
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.request.ApplicationRequest
import io.ktor.request.acceptItems
import io.ktor.request.contentType
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.util.pipeline.PipelineContext

abstract class OpenAPIRoute<T : OpenAPIRoute<T>>(val ktorRoute: Route, val provider: CachingModuleProvider) {

    abstract fun child(route: Route = this.ktorRoute): T

    inline fun <reified P : Any, reified B : Any, C : OpenAPIPipelineContext> handle(
            crossinline body: suspend C.(P, B) -> Unit,
            crossinline createContext: OpenAPIRoute<*>.(pipeline: PipelineContext<Unit, ApplicationCall>) -> C
    ) {
        val apiGen = ktorRoute.application.openAPIGen
        provider.ofClass<HandlerModule>().forEach {
            it.configure(apiGen, provider)
        }
        ktorRoute.handle {
            val contentType = context.request.contentType()
            val receive: B = if (Unit is B) Unit else getBodyParser(contentType).parseBody(B::class, this)
            val params: P = if (Unit is P) Unit else buildParameterObject(call, P::class.java)
            createContext(this).body(params, receive)
        }
    }

    companion object {
        private val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun <T> buildParameterObject(call: ApplicationCall, clazz: Class<T>): T {
            return mapper.convertValue(call.parameters.names().associateWith { call.parameters[it] }, clazz)
        }
    }

    fun getResponseSerializer(req: ApplicationRequest): ResponseSerializer {
        val accept = req.acceptItems()
        val serializers = provider.ofClass<SelectedSerializer>()
        if (accept.isEmpty()) {
            serializers.firstOrNull()?.module?.let { return it }
        } else {
            accept.forEach { acc ->
                serializers.firstOrNull { ser -> ser.module.accept(ContentType.parse(acc.value)) }?.let { return it.module }
            }
        }
        throw OpenAPINoSerializerException(accept.map { ContentType.parse(it.value) })
    }

    fun getBodyParser(contentType: ContentType): BodyParser {
        return provider.ofClass<SelectedParser>().firstOrNull()?.module ?: throw OpenAPINoParserException(contentType)
    }
}