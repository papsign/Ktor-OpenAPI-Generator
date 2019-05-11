package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.modules.providers.ThrowInfoProvider
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

data class ThrowsInfo(override val exceptions: List<APIException<*, *>>) : ThrowInfoProvider

inline fun <T: OpenAPIRoute<T>> T.throws(vararg responses: APIException<*, *>, crossinline fn: T.() -> Unit = {}): T {
    val next = child()
    next.provider.registerModule(ThrowsInfo(responses.asList()))
    val handler = makeExceptionHandler(responses)
    next.ktorRoute.intercept(ApplicationCallPipeline.Monitoring) {
        try {
            coroutineScope {
                proceed()
            }
        } catch (exception: Throwable) {
            if (call.response.status() == null) {
                handler(exception)
                if (call.response.status() != null) {
                    finish()
                }
            } else throw exception
        }
    }
    next.fn()
    return next
}

fun makeExceptionHandler(info: Array<out APIException<*, *>>): suspend PipelineContext<*, ApplicationCall>.(t: Throwable) -> Unit {
    val classes = info.associateBy { it.exceptionClass }
    fun findHandlerByType(clazz: KClass<*>): APIException<*, *>? {
        classes[clazz]?.let { return it }
        clazz.superclasses.forEach {
            findHandlerByType(it)?.let { return it }
        }
        return null
    }
    return { t: Throwable ->
        val handler: APIException<*, *> = findHandlerByType(t::class) ?: throw t
        val gen = handler.contentGen as ((Throwable) -> Any?)?
        if (gen != null) {
            val ret = gen(t)
            if (ret != null) {
                call.respond(handler.status, ret)
            } else {
                call.respond(handler.status)
            }
        } else {
            call.respond(handler.status)
        }
    }
}
