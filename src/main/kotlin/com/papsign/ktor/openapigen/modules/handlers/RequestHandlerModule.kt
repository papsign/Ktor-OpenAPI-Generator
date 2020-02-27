package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.SelectedParser
import com.papsign.ktor.openapigen.parameters.ParamBuilder
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.ParameterProvider
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

    private val log = classLogger()

    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {
        val map = provider.ofClass<BodyParser>().mapNotNull {
            val mediaType = it.getMediaType(requestType, apiGen, provider, requestExample, ContentTypeProvider.Usage.PARSE)
                    ?: return@mapNotNull null
            provider.registerModule(SelectedParser(it))
            mediaType.map { Pair(it.key.toString(), it.value) }
        }.flatten().associate { it }

        val requestMeta = requestClass.findAnnotation<Request>()

        val parameters = provider.ofClass<ParameterProvider>().flatMap { it.getParameters(ParamBuilder(apiGen, provider)) }
        operation.parameters = operation.parameters?.let { (it + parameters).distinct() } ?: parameters
        operation.requestBody = operation.requestBody?.apply {
            map.forEach { (key, value) ->
                content.putIfAbsent(key, value)?.let { if (value != it) log.warn("ContentType of $requestType request $key already registered, ignoring $value") }
            }
            if (description != null) {
                if (requestMeta?.description != null) log.warn("ContentType description of $requestType request already registered, ignoring")
            } else {
                description = requestMeta?.description
            }
        } ?: if (map.isNotEmpty()) RequestBody(map.toMutableMap(), description = requestMeta?.description) else null
    }

    companion object {
        inline fun <reified T : Any> create(requestExample: T? = null) =
                RequestHandlerModule(T::class, getKType<T>(), requestExample)
    }
}
