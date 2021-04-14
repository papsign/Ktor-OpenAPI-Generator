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
 * Create an exception handler passing in the HTTP return code.
 * The exception class is explicitly declared as generics parameter.
 *
 * Example:
 * <pre>
 * {@code
 *   throws<NormalOpenAPIRoute, IllegalArgumentException>(
 *      status = HttpStatusCode.NotFound.description("Customer not found")
 *   ) { /* add routes here */ }
 * }
 * </pre>
 *
 * @param status The HTTP status code to return
 * @param fn The lambda with your OpenAPIRoute as receiver to setup routes
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable> T.throws(
    status: HttpStatusCode,
    crossinline fn: T.() -> Unit = {}
): T = throws<T, EX, Unit>(status, fn = fn)

/**
 * Create an exception handler passing in the HTTP return code and the exception class.
 * The exception class is explicitly declared as function parameter.
 *
 * Example:
 * <pre>
 * {@code
 *   throws(
 *      status = HttpStatusCode.NotFound.description("Customer not found"),
 *      exceptionClass = IllegalArgumentException::class
 *   ) { /* add routes here */ }
 * }
 * </pre>
 *
 * @param status The HTTP status code to return
 * @param exceptionClass The exception class this handler will handle.
 * @param fn The lambda with your OpenAPIRoute as receiver to setup routes
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable> T.throws(
    status: HttpStatusCode,
    exceptionClass: KClass<EX>,
    crossinline fn: T.() -> Unit = {}
): T = throws<T, EX>(status, fn)

/**
 * Create an exception handler.
 * The exception class is explicitly declared as function parameter.
 *
 * Example:
 * <pre>
 * {@code
 *   throws(
 *      status = HttpStatusCode.NotFound.description("Customer not found"),
 *      example = ErrorMessage("Customer not found"),
 *      exceptionClass = IllegalArgumentException::class
 *   ) { /* add routes here */ }
 * }
 * </pre>
 *
 * @param status The HTTP status code to return
 * @param example An example of the HTTP response
 * @param exceptionClass The exception class this handler will handle.
 * @param fn The lambda with your OpenAPIRoute as receiver to setup routes
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable, reified B> T.throws(
    status: HttpStatusCode,
    example: B? = null,
    exceptionClass: KClass<EX>,
    crossinline fn: T.() -> Unit = {}
) = throws<T, EX, B>(status, example, fn = fn)

/**
 * Create an exception handler.
 * The exception class is inferred from the contentFn function.
 *
 * Example:
 * <pre>
 * {@code
 *   throws(
 *      status = HttpStatusCode.NotFound.description("Customer not found"),
 *      example = ErrorMessage("Customer not found"),
 *      contentFn = { ex: IllegalArgumentException ->
 *          ErrorMessage(ex.message ?: "Customer with uuid 300e47ad-263c-4e9a-a0b7-26d1229eaba8 not found")
 *      }
 *   ) { /* add routes here */ }
 * }
 * </pre>
 *
 * @param status The HTTP status code to return
 * @param example An example of the HTTP response
 * @param contentFn The function that creates the HTTP response
 * @param fn The lambda with your OpenAPIRoute as receiver to setup routes
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable, reified B> T.throws(
    status: HttpStatusCode,
    example: B? = null,
    noinline contentFn: ((EX) -> B)? = null,
    crossinline fn: T.() -> Unit = {}
): T = throws(APIException.apiException(status, example, contentFn), fn = fn)

/**
 * Create an exception handler.
 * The exception class is explicitly declared as function parameter.
 *
 * Example:
 * <pre>
 * {@code
 *   throws(
 *      status = HttpStatusCode.NotFound.description("Customer not found"),
 *      example = ErrorMessage("Customer with uuid 300e47ad-263c-4e9a-a0b7-26d1229eaba8 not found"),
 *      exceptionClass = IllegalArgumentException::class,
 *      contentFn = { ErrorMessage(it.message ?: "Customer not found") }
 *   ) { /* add routes here */ }
 * }
 * </pre>
 *
 * @param status The HTTP status code to return
 * @param example An example of the HTTP response
 * @param exceptionClass The exception class this handler will handle.
 * @param contentFn The function that creates the HTTP response
 * @param fn The lambda with your OpenAPIRoute as receiver to setup routes
 */
inline fun <T: OpenAPIRoute<T>, reified EX : Throwable, reified B> T.throws(
    status: HttpStatusCode,
    example: B? = null,
    exceptionClass: KClass<EX>,
    noinline contentFn: ((EX) -> B)? = null,
    crossinline fn: T.() -> Unit = {}
): T = throws(APIException.apiException(status, example, contentFn), fn = fn)

inline fun <T: OpenAPIRoute<T>> T.throws(
    vararg responses: APIException<*, *>,
    crossinline fn: T.() -> Unit = {}
): T = child(ktorRoute.createConstantChild()).apply {
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

fun makeExceptionHandler(
    info: Array<out APIException<*, *>>
): suspend PipelineContext<*, ApplicationCall>.(t: Throwable) -> Unit {

    val classes = info.associateBy { it.exceptionClass }

    fun findHandlerByType(clazz: KClass<*>): APIException<*, *>? {
        classes[clazz]?.let { return it }
        clazz.superclasses.forEach { superClazz ->
            findHandlerByType(superClazz)?.let { return it }
        }
        return null
    }

    return { t: Throwable ->
        val handler: APIException<*, *> = findHandlerByType(t::class) ?: throw t
        val gen = handler.contentFn as ((Throwable) -> Any?)?
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
            ex != null -> call.respond(handler.status, ex)
            else -> call.respond(handler.status)
        }
    }
}
