package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.exceptions.OpenAPIParseException
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.openapi.DataFormat
import com.papsign.ktor.openapigen.openapi.DataType
import com.papsign.ktor.openapigen.openapi.MediaType
import com.papsign.ktor.openapigen.openapi.Schema
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.request.receiveStream
import io.ktor.util.pipeline.PipelineContext
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.KType

class BinaryContentTypeParser(override val contentType: ContentType): BodyParser {

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> parseBody(clazz: KClass<T>, request: PipelineContext<Unit, ApplicationCall>): T {
        return when (clazz) {
            ByteArray::class -> request.context.receiveStream().readBytes() as T
            InputStream::class -> request.context.receiveStream() as T
            else -> throw OpenAPIParseException(clazz, acceptedClasses)
        }
    }

    override fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T?): MediaType<T>? {
        if (!acceptedTypes.contains(type)) return null
        return MediaType(Schema.SchemaLitteral(DataType.string, DataFormat.binary), example)
    }

    companion object {
        private val acceptedTypes = setOf(getKType<ByteArray>(), getKType<InputStream>())
        private val acceptedClasses = setOf(ByteArray::class, InputStream::class)
    }
}