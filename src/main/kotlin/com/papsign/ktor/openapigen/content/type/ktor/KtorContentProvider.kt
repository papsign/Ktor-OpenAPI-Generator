package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.unitKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.annotations.encodings.APIRequestFormat
import com.papsign.ktor.openapigen.annotations.encodings.APIResponseFormat
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.model.operation.MediaTypeModel
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.schema.builder.provider.FinalSchemaBuilderProviderModule
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.featureOrNull
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

/**
 * default content provider using the ktor pipeline to handle the serialization and deserialization
 */
object KtorContentProvider : ContentTypeProvider, BodyParser, ResponseSerializer, OpenAPIGenModuleExtension {

    private var contentNegotiation: ContentNegotiation? = null
    private var contentTypes: Set<ContentType>? = null

    private fun initContentTypes(apiGen: OpenAPIGen): Set<ContentType>? {
        contentNegotiation = contentNegotiation ?: apiGen.pipeline.featureOrNull(ContentNegotiation) ?: return null
        contentTypes = contentNegotiation!!.registrations.map { it.contentType }.toSet()
        return contentTypes
    }

    override fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T?, usage: ContentTypeProvider.Usage):Map<ContentType, MediaTypeModel<T>>? {
        if (type == unitKType) return null
        val clazz = type.jvmErasure
        when (usage) { // check if it is explicitly declared or none is present
            ContentTypeProvider.Usage.PARSE -> when {
                clazz.findAnnotation<KtorRequest>() != null -> {}
                clazz.annotations.none { it.annotationClass.findAnnotation<APIRequestFormat>() != null } -> {}
                else -> return null
            }
            ContentTypeProvider.Usage.SERIALIZE -> when {
                clazz.findAnnotation<KtorResponse>() != null -> {}
                clazz.annotations.none { it.annotationClass.findAnnotation<APIResponseFormat>() != null } -> {}
                else -> return null
            }
        }
        val contentTypes = initContentTypes(apiGen) ?: return null
        val schemaBuilder = provider.ofClass<FinalSchemaBuilderProviderModule>().last().provide(apiGen, provider)
        @Suppress("UNCHECKED_CAST")
        val media =  MediaTypeModel(schemaBuilder.build(type) as SchemaModel<T>, example)
        return contentTypes.associateWith { media.copy() }
    }

    override fun <T : Any> getParseableContentTypes(clazz: KClass<T>): List<ContentType> {
        return contentTypes!!.toList()
    }

    override suspend fun <T: Any> parseBody(clazz: KType, request: PipelineContext<Unit, ApplicationCall>): T {
        return request.call.receive(clazz)
    }

    override fun <T: Any> getSerializableContentTypes(clazz: KClass<T>): List<ContentType> {
        return contentTypes!!.toList()
    }

    override suspend fun <T: Any> respond(response: T, request: PipelineContext<Unit, ApplicationCall>, contentType: ContentType) {
        request.call.respond(response)
    }

    override suspend fun <T: Any> respond(statusCode: HttpStatusCode, response: T, request: PipelineContext<Unit, ApplicationCall>, contentType: ContentType) {
        request.call.respond(statusCode, response)
    }
}
