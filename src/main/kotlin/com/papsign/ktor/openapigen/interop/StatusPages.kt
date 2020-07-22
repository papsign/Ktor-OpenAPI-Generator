package com.papsign.ktor.openapigen.interop

import com.papsign.ktor.openapigen.APIException.Companion.apiException
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.registerModule
import com.papsign.ktor.openapigen.route.ThrowsInfo
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond

/**
 * Wraps [StatusPages.Configuration] to enable OpenAPI configuration for exception handling.
 * ```
 *  val api = install(OpenAPIGen)  { ... }
 *  ...
 *  // StatusPage interop, can also define exceptions per-route
 *  install(StatusPages) {
 *      withAPI(api) {
 *          exception<JsonMappingException, Error>(HttpStatusCode.BadRequest) {
 *              it.printStackTrace()
 *              Error("mapping.json", it.localizedMessage)
 *          }
 *          exception<ProperException, Error>(HttpStatusCode.BadRequest) {
 *              it.printStackTrace()
 *              Error(it.id, it.localizedMessage)
 *          }
 *      }
 *  }
 * ```
 *
 * @param api the installed instance of [OpenAPIGen] in the [io.ktor.application.Application]
 * @param cfg the block that loads the configuration, see [OpenAPIGenStatusPagesInterop]
 */
inline fun StatusPages.Configuration.withAPI(api: OpenAPIGen, crossinline cfg: OpenAPIGenStatusPagesInterop.() -> Unit = {}) {
    OpenAPIGenStatusPagesInterop(api, this).cfg()
}

/**
 * Wrapper for Status pages that handles exceptions and generates documentation in OpenAPI.
 * This is useful for default error pages.
 */
class OpenAPIGenStatusPagesInterop(val api: OpenAPIGen, val statusCfg: StatusPages.Configuration) {

    /**
     * Registers a handler for exception type [TThrowable] and returns a [status] page
     */
    inline fun <reified TThrowable : Throwable> exception(status: HttpStatusCode) {
        val ex = apiException<TThrowable>(status)
        api.globalModuleProvider.registerModule(ThrowsInfo(listOf(ex)))
        statusCfg.exception<TThrowable> {
            call.respond(status)
        }
    }

    /**
     * Registers a handler for exception type [TThrowable] and returns a [status] page that includes a response body
     * of type [TResponse].
     *
     * @param example An example of the response body
     * @param gen handler for [TThrowable] that should return an instance of [TResponse]
     */
    inline fun <reified TThrowable : Throwable, reified TResponse> exception(status: HttpStatusCode, example: TResponse? = null, noinline gen: (TThrowable) -> TResponse) {
        val ex = apiException(status, example, gen)
        api.globalModuleProvider.registerModule(ThrowsInfo(listOf(ex)))
        statusCfg.exception<TThrowable> { t ->
            val ret = gen(t)
            if (ret != null) {
                call.respond(status, ret)
            } else {
                call.respond(status)
            }
        }
    }
}
