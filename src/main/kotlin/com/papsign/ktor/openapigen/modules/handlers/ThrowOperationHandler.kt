package com.papsign.ktor.openapigen.modules.handlers

import com.papsign.ktor.openapigen.unitKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.content.type.SelectedExceptionSerializer
import com.papsign.ktor.openapigen.model.operation.MediaTypeModel
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.model.operation.StatusResponseModel
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.modules.openapi.OperationModule
import com.papsign.ktor.openapigen.modules.providers.ThrowInfoProvider

object ThrowOperationHandler : OperationModule {
    private val log = classLogger()
    override fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: OperationModel) {

        val exceptions = provider.ofClass<ThrowInfoProvider>().flatMap { it.exceptions }
        exceptions.groupBy { it.status }.forEach { exceptions ->
            val map: MutableMap<String, MediaTypeModel<*>> = exceptions.value.flatMap { ex ->
                provider.ofClass<ResponseSerializer>().mapNotNull {
                    if (ex.contentType == unitKType) return@mapNotNull null
                    val mediaType = it.getMediaType(ex.contentType, apiGen, provider, ex.example, ContentTypeProvider.Usage.SERIALIZE) ?: return@mapNotNull null
                    provider.registerModule(SelectedExceptionSerializer(it))
                    mediaType.map { Pair(it.key.toString(), it.value) }
                }
            }.flatten().groupBy { it.first }.mapValues {
                val schemas = it.value.mapNotNull { it.second.schema }.distinct()
                val schema = when {
                    schemas.isEmpty() -> null
                    schemas.size == 1 -> schemas.first()
                    else -> SchemaModel.OneSchemaModelOf(schemas)
                }
                val examples =  it.value.mapNotNull { (_, second) -> second.example }.withIndex().associate { (idx, value) -> "Example $idx" to value }.toMutableMap()
                if (examples.size <= 1) {
                    MediaTypeModel(schema, example = examples.values.firstOrNull())
                } else {
                    MediaTypeModel(schema, examples = examples)
                }
            }.toMutableMap()
            val statusCode = exceptions.key
            val status = statusCode.value.toString()
            operation.responses[status] = operation.responses[status]?.apply {
                map.forEach { (key, value) ->
                    content.putIfAbsent(key, value)?.let { if (value != it) log.warn("Cannot map Exception handler on $status with type $key, it is already in use by ${statusCode.description(description)}") }
                }
            } ?: StatusResponseModel(statusCode.description, content = map.toMutableMap())
        }
    }
}
