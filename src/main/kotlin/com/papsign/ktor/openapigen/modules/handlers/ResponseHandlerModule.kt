package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.content.type.SelectedSerializer
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.model.operation.StatusResponseModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofType
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.StatusProvider
import com.papsign.ktor.openapigen.modules.registerModule
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType

class ResponseHandlerModule<T>(val responseType: KType, val responseExample: T? = null) : OperationModule {
    private val log = classLogger()
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: OperationModel) {
        val responseMeta = (responseType.classifier as? KAnnotatedElement)?.findAnnotation<Response>()
        val statusCode =  provider.ofType<StatusProvider>().lastOrNull()?.getStatusForType(responseType) ?: responseMeta?.statusCode?.let { HttpStatusCode.fromValue(it) }
        ?: HttpStatusCode.OK
        val status = statusCode.value.toString()
        val map = provider.ofType<ResponseSerializer>().mapNotNull {
            val mediaType = it.getMediaType(responseType, apiGen, provider, responseExample, ContentTypeProvider.Usage.SERIALIZE)
                    ?: return@mapNotNull null
            provider.registerModule(SelectedSerializer(it))
            mediaType.map { Pair(it.key.toString(), it.value) }
        }.flatten().associate { it }
        val descstr = responseMeta?.description ?: statusCode.description
        operation.responses[status] = operation.responses[status]?.apply {
            map.forEach { (key, value) ->
                content.putIfAbsent(key, value)?.let { if (value != it) log.warn("ContentType of $responseType response $key already registered, ignoring $value") }
            }
            if (description != statusCode.description) {
                if (responseMeta?.description != null) log.warn("ContentType description of $responseType response already registered, ignoring")
            } else {
                description = responseMeta?.description ?: statusCode.description
            }
        } ?: StatusResponseModel(descstr, content = map.toMutableMap())
    }

    companion object {
        inline fun <reified T : Any> create(responseExample: T? = null) = ResponseHandlerModule(getKType<T>(), responseExample)
        fun <T : Any> create(tClass: KClass<T>, responseExample: T? = null) = ResponseHandlerModule(tClass.starProjectedType, responseExample)
    }
}
