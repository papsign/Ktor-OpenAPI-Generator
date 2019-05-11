package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.ktor.openapigen.content.type.ResponseSerializer
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

object KtorJSONSerializer: KtorJSONContentProvider(), ResponseSerializer {

    override suspend fun <T: Any> respond(response: T, request: PipelineContext<Unit, ApplicationCall>) {
        request.call.respond(response)
    }

    override suspend fun <T: Any> respond(statusCode: HttpStatusCode, response: T, request: PipelineContext<Unit, ApplicationCall>) {
        request.call.respond(statusCode, response)
    }
}