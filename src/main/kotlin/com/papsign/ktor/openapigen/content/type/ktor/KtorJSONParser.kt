package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.ktor.openapigen.content.type.BodyParser
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.util.pipeline.PipelineContext
import kotlin.reflect.KClass

object KtorJSONParser: KtorJSONContentProvider(), BodyParser {

    override suspend fun <T: Any> parseBody(clazz: KClass<T>, request: PipelineContext<Unit, ApplicationCall>): T {
        return request.call.receive(clazz)
    }
}