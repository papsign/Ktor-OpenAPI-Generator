package com.papsign.ktor.openapigen.content.type

import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass

interface BodyParser: ContentTypeProvider {
    suspend fun <T: Any> parseBody(clazz: KClass<T>, request: PipelineContext<Unit, ApplicationCall>): T
}