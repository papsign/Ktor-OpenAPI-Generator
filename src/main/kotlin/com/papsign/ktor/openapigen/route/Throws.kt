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
 * Create an exception handler.
 *
 * If all parameters are passed in, the type parameters are inferred.
 * Example:
 * <pre>
 * {@code
 *   throws(
 *      status = HttpStatusCode.NotFound.description("Customer not found"),
 *      example = ErrorMessage("Customer with uuid 300e47ad-263c-4e9a-a0b7-26d1229eaba8 not found"),
 *      contentFn = { ex: NotFoundException -> ErrorMessage(ex.message ?: "Customer not found") }
 *   ) { /* add route here */ }
 *
 * If not all parameters are passed, the type parameters needs to be explicitly named.
 * Example:
 * <pre>
 * {@code
 *   throws<NormalOpenAPIRoute, NotFoundException, ErrorMessage>(
 *      status = HttpStatusCode.NotFound.description("Customer not found"),
 *   ) { getOneCustomer(this) }
 * }
 * </pre>
 *
 * @param status The HTTP status code to return
 * @param example An example of the HTTP response
 * @param contentFn The function that creates the HTTP response
 * @param fn The lambda with your OpenAPIRoute as receiver to setup routes
 */
inline fun <TRoute: OpenAPIRoute<TRoute>, reified TException : Throwable, reified TMessage> TRoute.throws(
    status: HttpStatusCode,
    example: TMessage? = null,
    noinline contentFn: ((TException) -> TMessage)? = null,
    crossinline fn: TRoute.() -> Unit = {}
): TRoute {
    val apiException = APIException.apiException(status, example, contentFn)
    return throws(apiException, fn = fn)
}

/**
 * Create one or multiple exception handler(s).
 * The exception handlers (responses parameter can either be created
 * 1. APIException.Companion.apiException: direct object creation
 * 2. APIExceptionBuilder.Companion.apiException: object creation using the builder pattern
 *
 * Example 1:
 * <pre>
 * {@code
 *    throws(
 *      APIException.apiException(
 *         status = HttpStatusCode.NotFound.description("Customer not found"),
 *         example = ErrorMessage("Customer with id 756 not found"),
 *         contentFn = { ex: NotFoundException -> ErrorMessage(ex.message ?: "Customer not found") }
 *      ),
 *      APIException.apiException<ValidationException, ErrorMessage>(
 *         status = HttpStatusCode.BadRequest.description("Invalid Request"),
 *         example = ErrorMessage("Parameter too short")
 *      )
 *    ) { /* add routes here */ }
 * </pre>
 *
 * Example 2 (builder):
 * <pre>
 * {@code
 *    throws(
 *       apiException<ValidationException, ErrorMessage> {
 *          status = HttpStatusCode.BadRequest.description("Invalid Request")
 *          example = ErrorMessage("parameter doesn't match \"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\$\"")
 *          contentFn = { ErrorMessage(it.message ?: "Bad Request" ) }
 *       },
 *       apiException<NotFoundException, ErrorMessage> {
 *          status = HttpStatusCode.NotFound.description("Customer not found")
 *          contentFn = { ErrorMessage(it.message ?: "Customer not found") }
 *       }
 *    ) { /* add routes here */ }
 * </pre>
 */
inline fun <TRoute: OpenAPIRoute<TRoute>> TRoute.throws(
    vararg responses: APIException<*, *>,
    crossinline fn: TRoute.() -> Unit = {}
): TRoute = child(ktorRoute.createConstantChild()).apply {
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
