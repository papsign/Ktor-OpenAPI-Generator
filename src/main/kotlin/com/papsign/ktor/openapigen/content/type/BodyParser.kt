package com.papsign.ktor.openapigen.content.type

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass

interface BodyParser: ContentTypeProvider {
    fun <T: Any> getParseableContentTypes(clazz: KClass<T>): List<ContentType>
    suspend fun <T: Any> parseBody(clazz: KClass<T>, request: PipelineContext<Unit, ApplicationCall>): T
}