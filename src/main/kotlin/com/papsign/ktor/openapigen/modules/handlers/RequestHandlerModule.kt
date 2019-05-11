package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.openapi.MediaType
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.RequestBody
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

class RequestHandlerModule<T : Any>(
    val requestClass: KClass<T>,
    val requestType: KType,
    val requestExample: T? = null
) : OperationModule {
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {
        operation.requestBody =
            RequestBody(
                provider.ofClass<BodyParser>().mapNotNull {
                    val mediaType = it.getMediaType(requestType, apiGen, provider, requestExample)
                    if (mediaType == null) {
                        provider.unRegisterModule(it)
                        null
                    } else {
                        Pair(it.contentType.toString(), mediaType)
                    }
                }.fold(mutableMapOf<String, MediaType<T>>()) { a, b -> a[b.first] = b.second; a },
                description = requestClass.findAnnotation<Request>()?.description
            )
    }

    companion object {
        inline fun <reified T : Any> create(requestExample: T? = null) =
            RequestHandlerModule(T::class, getKType<T>(), requestExample)
    }
}