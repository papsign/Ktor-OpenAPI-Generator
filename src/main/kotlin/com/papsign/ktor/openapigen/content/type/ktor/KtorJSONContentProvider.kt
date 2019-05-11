package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.kotlin.reflection.allTypes
import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
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
import kotlin.reflect.full.withNullability

abstract class KtorJSONContentProvider : ContentTypeProvider {

    override val contentType: ContentType = ContentType.Application.Json

    companion object {
        private val arrayType = getKType<ByteArray>()
    }

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

    override fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T?): MediaType<T> {
        val reg = if (type.allTypes().contains(arrayType)) {
            Registrar(SimpleSchemaRegistrar(apiGen.schemaRegistrar.namer))
        } else apiGen.schemaRegistrar
        return MediaType(reg[type].schema as Schema<T>, example)
    }
}