package com.papsign.ktor.openapigen.content.type

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass

interface ResponseSerializer: ContentTypeProvider {
    /**
     * used to determine which registered response serializer is used, based on the accept header
     */
    fun <T: Any> getSerializableContentTypes(clazz: KClass<T>): List<ContentType>
    suspend fun <T: Any> respond(response: T, request: PipelineContext<Unit, ApplicationCall>, contentType: ContentType)
    suspend fun <T: Any> respond(statusCode: HttpStatusCode, response: T, request: PipelineContext<Unit, ApplicationCall>, contentType: ContentType)
}
