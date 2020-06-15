package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.modules.providers.ThrowInfoProvider
import com.papsign.ktor.openapigen.modules.registerModule
import com.papsign.ktor.openapigen.route.util.createConstantChild
import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass
import kotlin.reflect.full.superclasses

data class ThrowsInfo(override val exceptions: List<APIException<*, *>>) : ThrowInfoProvider

/**
 * exists for simpler syntax
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable> T.throws(status: HttpStatusCode, exClass: KClass<EX>, crossinline fn: T.() -> Unit = {}): T {
    return throws<T, EX>(status, fn)
}

inline fun <T: OpenAPIRoute<T>, reified EX : Throwable> T.throws(status: HttpStatusCode, crossinline fn: T.() -> Unit = {}): T {
    return throws<T, EX, Unit>(status, fn = fn)
}

/**
 * exists for simpler syntax
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable, reified B> T.throws(status: HttpStatusCode, example: B? = null, exClass: KClass<EX>, crossinline fn: T.() -> Unit = {}): T {
    return throws<T, EX, B>(status, example, null, fn)
}

inline fun <T: OpenAPIRoute<T>, reified EX : Throwable, reified B> T.throws(status: HttpStatusCode, example: B? = null, noinline gen: ((EX) -> B)? = null, crossinline fn: T.() -> Unit = {}): T {
    return throws(APIException.apiException(status, example, gen), fn = fn)
}

inline fun <T: OpenAPIRoute<T>> T.throws(vararg responses: APIException<*, *>, crossinline fn: T.() -> Unit = {}): T {
    return child(ktorRoute.createConstantChild()).apply {
        provider.registerModule(ThrowsInfo(responses.asList()))
        val handler = makeExceptionHandler(responses)
        ktorRoute.intercept(ApplicationCallPipeline.Monitoring) {
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
        fn()
    }
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
        val ex = handler.example
        when {
            gen != null -> {
                val ret = gen(t)
                if (ret != null) {
                    call.respond(handler.status, ret)
                } else {
                    call.respond(handler.status)
                }
            }
            ex != null -> {
                call.respond(handler.status, ex)
            }
            else -> {
                call.respond(handler.status)
            }
        }
    }
}
