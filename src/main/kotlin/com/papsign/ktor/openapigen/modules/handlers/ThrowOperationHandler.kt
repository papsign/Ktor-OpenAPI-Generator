package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.kotlin.reflection.unitKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.content.type.SelectedSerializer
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.ThrowInfoProvider
import com.papsign.ktor.openapigen.openapi.MediaType
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.Schema
import com.papsign.ktor.openapigen.openapi.StatusResponse

object ThrowOperationHandler : OperationModule {
    private val log = classLogger()
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {

        val exceptions = provider.ofClass<ThrowInfoProvider>().flatMap { it.exceptions }
        exceptions.groupBy { it.status }.forEach { exceptions ->
            val map: MutableMap<String, MediaType<*>> = exceptions.value.flatMap { ex ->
                provider.ofClass<ResponseSerializer>().mapNotNull {
                    if (ex.contentType == unitKType) return@mapNotNull null
                    val mediaType = it.getMediaType<Any>(ex.contentType, apiGen, provider, null, ContentTypeProvider.Usage.SERIALIZE) ?: return@mapNotNull null
                    provider.registerModule(SelectedSerializer(it))
                    mediaType.map { Pair(it.key.toString(), it.value) }
                }
            }.flatten().groupBy { it.first }.mapValues {
                val schemas = it.value.mapNotNull { it.second.schema }.distinct()
                val schema = when {
                    schemas.isEmpty() -> null
                    schemas.size == 1 -> schemas.first()
                    else -> Schema.OneSchemaOf(schemas)
                }
                MediaType(schema)
            }.toMutableMap()
            val statusCode = exceptions.key
            val status = statusCode.value.toString()
            operation.responses[status] = operation.responses[status]?.apply {
                map.forEach { (key, value) ->
                    content.putIfAbsent(key, value)?.let { if (value != it) log.warn("Cannot map Exception handler on $status with type $key, it is already in use by ${statusCode.description(description)}") }
                }
            } ?: StatusResponse(statusCode.description, content = map.toMutableMap())
        }
    }
}