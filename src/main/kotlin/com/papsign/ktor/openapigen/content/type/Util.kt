package com.papsign.ktor.openapigen.content.type

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.openapi.MediaType
import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

inline fun <reified T> ContentTypeProvider.getMediaType(apiGen: OpenAPIGen, provider: ModuleProvider<*>): MediaType<T>? {
    return getMediaType(getKType<T>(), apiGen, provider)
}

suspend inline fun <reified T: Any> BodyParser.parseBody(request: PipelineContext<Unit, ApplicationCall>): T {
    return parseBody(T::class, request)
}