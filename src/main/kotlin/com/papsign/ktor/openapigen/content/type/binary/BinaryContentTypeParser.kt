package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.kotlin.reflection.getKType
import com.papsign.kotlin.reflection.getObjectSubtypes
import com.papsign.kotlin.reflection.unitKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.encodings.APIFormatter
import com.papsign.ktor.openapigen.content.type.BodyParser
import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import com.papsign.ktor.openapigen.exceptions.assertContent
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.openapi.DataFormat
import com.papsign.ktor.openapigen.openapi.DataType
import com.papsign.ktor.openapigen.openapi.MediaType
import com.papsign.ktor.openapigen.openapi.Schema
import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveStream
import io.ktor.response.respondBytes
import io.ktor.util.pipeline.PipelineContext
import java.io.InputStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

@APIFormatter
object BinaryContentTypeParser: BodyParser, ResponseSerializer {

    override fun <T : Any> getParseableContentTypes(clazz: KClass<T>): List<ContentType> {
        return clazz.findAnnotation<BinaryRequest>()?.contentTypes?.map(ContentType.Companion::parse) ?: listOf()
    }

    override fun <T: Any> getSerializableContentTypes(clazz: KClass<T>):  List<ContentType> {
        return clazz.findAnnotation<BinaryResponse>()?.contentTypes?.map(ContentType.Companion::parse) ?: listOf()
    }

    override suspend fun <T : Any> respond(response: T, request: PipelineContext<Unit, ApplicationCall>, contentType: ContentType) {
        val code = response::class.findAnnotation<Response>()?.statusCode?.let { HttpStatusCode.fromValue(it) } ?: HttpStatusCode.OK
        respond(code, response, request, contentType)
    }

    override suspend fun <T : Any> respond(statusCode: HttpStatusCode, response: T, request: PipelineContext<Unit, ApplicationCall>, contentType: ContentType) {
        @Suppress("UNCHECKED_CAST")
        val prop = response::class.declaredMemberProperties.first { it.visibility == KVisibility.PUBLIC } as KProperty1<T, *>
        val data = prop.get(response) as InputStream
        request.context.respondBytes(data.readBytes(), contentType, statusCode)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> parseBody(clazz: KClass<T>, request: PipelineContext<Unit, ApplicationCall>): T {
        return clazz.constructors.first { it.parameters.size == 1 && acceptedTypes.contains(it.parameters[0].type) }.call( request.context.receiveStream())
    }

    override fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T?, usage: ContentTypeProvider.Usage): Map<ContentType, MediaType<T>>? {
        if (type == unitKType) return null
        val contentTypes = when(usage) {
            ContentTypeProvider.Usage.PARSE -> {
                val binaryRequest = type.jvmErasure.findAnnotation<BinaryRequest>() ?: return null
                binaryRequest.contentTypes
            }
            ContentTypeProvider.Usage.SERIALIZE -> {
                val binaryRequest = type.jvmErasure.findAnnotation<BinaryResponse>() ?: return null
                binaryRequest.contentTypes
            }
        }.also {
            it.forEach { ContentType.parse(it) }
        }
        val subtypes = type.getObjectSubtypes()
        assertContent (acceptedTypes.containsAll(subtypes)) {
            "${this::class.simpleName} can only be used with type ${acceptedTypes.joinToString()}, you are using ${subtypes.minus(acceptedTypes)}"
        }
        when(usage) {
            ContentTypeProvider.Usage.PARSE -> {
                assertContent (type.jvmErasure.constructors.find { it.parameters.size == 1 && acceptedTypes.contains(it.parameters[0].type) } != null) {
                    "${this::class.simpleName} can only be used with types taking $acceptedTypes as constructor parameter"
                }
            }
            ContentTypeProvider.Usage.SERIALIZE -> {
                val public = type.jvmErasure.declaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }
                assertContent(public.size == 1 && public.all { acceptedTypes.contains(it.returnType) }) {
                    "${this::class.simpleName} must provide exactly 1 public member property of type $acceptedTypes"
                }
            }
        }
        val mediaType = MediaType(Schema.SchemaLitteral(DataType.string, DataFormat.binary), example)
        return contentTypes.map(ContentType.Companion::parse).associateWith { mediaType.copy() }
    }

    private val acceptedTypes = setOf(getKType<InputStream>())
}