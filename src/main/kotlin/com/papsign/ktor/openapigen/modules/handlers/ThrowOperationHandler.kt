package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.ThrowInfoProvider
import com.papsign.ktor.openapigen.openapi.MediaType
import com.papsign.ktor.openapigen.openapi.Operation
import com.papsign.ktor.openapigen.openapi.Schema
import com.papsign.ktor.openapigen.openapi.StatusResponse

object ThrowOperationHandler: OperationModule {
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: Operation) {

        val exceptions = provider.ofClass<ThrowInfoProvider>().flatMap { it.exceptions }
        exceptions.groupBy { it.status }.forEach { exceptions ->
            val mediaTypes: MutableMap<String, MediaType<*>> = exceptions.value.flatMap { ex ->
                provider.ofClass<ResponseSerializer>().mapNotNull {
                    val mediaType = it.getMediaType<Any>(ex.contentType, apiGen, provider)
                    if (mediaType == null) {
                        provider.unRegisterModule(it)
                        null
                    } else {
                        Pair(it.contentType.toString(), mediaType)
                    }
                }
            }.groupBy { it.first }.mapValues {
                val schemas = it.value.mapNotNull { it.second.schema }.distinct()
                val schema = when {
                    schemas.isEmpty() -> null
                    schemas.size == 1 -> schemas.first()
                    else -> Schema.OneSchemaOf(schemas)
                }
                 MediaType(schema)
            }.toMutableMap()
            val status = exceptions.key
            val prev = operation.responses[status.value.toString()]
            if (prev != null) error("Error Mapping Exception handlers on $status, it is already in use by ${status.description(prev.description)}")
            operation.responses[status.value.toString()] = StatusResponse(status.description, content = mediaTypes)
        }
    }
}