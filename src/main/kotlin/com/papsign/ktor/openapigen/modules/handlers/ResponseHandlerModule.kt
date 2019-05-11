package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.StatusResponse
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

class ResponseHandlerModule<T: Any>(val responseClass: KClass<T>, val responseType: KType, val responseExample: T? = null): OperationModule {
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {

        val description = responseClass.findAnnotation<Response>()
        val statusCode = description?.statusCode?.let { HttpStatusCode.fromValue(it) } ?: HttpStatusCode.OK
        operation.responses[statusCode.value.toString()] = StatusResponse(
            description?.description ?: statusCode.description,
            content = provider.ofClass<ResponseSerializer>().mapNotNull {
                val mediaType = it.getMediaType(responseType, apiGen, provider, responseExample)
                if (mediaType == null) {
                    provider.unRegisterModule(it)
                    null
                } else {
                    Pair(it.contentType.toString(), mediaType)
                }
            }.fold(mutableMapOf()) { a, b -> a[b.first] = b.second; a }
        )
    }
    companion object {
        inline fun <reified T: Any> create(responseExample: T? = null) = ResponseHandlerModule(T::class, getKType<T>(), responseExample)
    }
}