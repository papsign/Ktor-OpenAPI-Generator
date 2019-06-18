package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.kotlin.reflection.allTypes
import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.encodings.APIEncoding
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.schema.NamedSchema
import com.papsign.ktor.openapigen.modules.schema.SchemaRegistrar
import com.papsign.ktor.openapigen.modules.schema.SimpleSchemaRegistrar
import com.papsign.ktor.openapigen.openapi.DataFormat
import com.papsign.ktor.openapigen.openapi.DataType
import com.papsign.ktor.openapigen.openapi.MediaType
import com.papsign.ktor.openapigen.openapi.Schema
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

/**
 * default content provider using the ktor pipeline to handle the serialization and deserialization
 */
@APIEncoding
object KtorContentProvider : ContentTypeProvider, BodyParser, ResponseSerializer {

    private val contentType = ContentType.Application.Json
    private val arrayType = getKType<ByteArray>()

    private class Registrar(val previous: SchemaRegistrar) : SchemaRegistrar {

        override fun get(type: KType, master: SchemaRegistrar): NamedSchema {
            return if (type == arrayType.withNullability(type.isMarkedNullable)) {
                NamedSchema(
                    "Base64ByteArray", Schema.SchemaLitteral(
                        DataType.string,
                        DataFormat.byte,
                        type.isMarkedNullable,
                        null,
                        null
                    )
                )
            } else previous[type, master]
        }
    }

    override fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T?, usage: ContentTypeProvider.Usage):Map<ContentType, MediaType<T>>? {
        if (type.jvmErasure.annotations.find { it.annotationClass.findAnnotation<APIEncoding>() != null } != null) return null //fallback
        val reg = if (type.allTypes().contains(arrayType)) {
            Registrar(SimpleSchemaRegistrar(apiGen.schemaRegistrar.namer))
        } else apiGen.schemaRegistrar

        @Suppress("UNCHECKED_CAST")
        return mapOf(contentType to MediaType(reg[type].schema as Schema<T>, example))
    }

    override suspend fun <T: Any> parseBody(clazz: KClass<T>, request: PipelineContext<Unit, ApplicationCall>): T {
        return request.call.receive(clazz)
    }

    override fun accept(contentType: ContentType): Boolean = this.contentType.match(contentType)

    override suspend fun <T: Any> respond(response: T, request: PipelineContext<Unit, ApplicationCall>) {
        request.call.respond(response)
    }

    override suspend fun <T: Any> respond(statusCode: HttpStatusCode, response: T, request: PipelineContext<Unit, ApplicationCall>) {
        request.call.respond(statusCode, response)
    }
}