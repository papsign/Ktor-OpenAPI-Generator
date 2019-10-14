package com.papsign.ktor.openapigen.interop

import com.papsign.ktor.openapigen.APIException.Companion.apiException
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.route.ThrowsInfo
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond


inline fun StatusPages.Configuration.withAPI(api: OpenAPIGen, crossinline cfg: OpenAPIGenStatusPagesInterop.() -> Unit = {}) {
    OpenAPIGenStatusPagesInterop(api, this).cfg()
}

class OpenAPIGenStatusPagesInterop(val api: OpenAPIGen, val statusCfg: StatusPages.Configuration) {

    inline fun <reified EX : Throwable> exception(status: HttpStatusCode) {
        val ex = apiException<EX>(status)
        api.globalModuleProvider.registerModule(ThrowsInfo(listOf(ex)))
        statusCfg.exception<EX> {
            call.respond(status)
        }
    }

    inline fun <reified EX : Throwable, reified B> exception(status: HttpStatusCode, noinline gen: (EX) -> B) {
        val ex = apiException(status, gen)
        api.globalModuleProvider.registerModule(ThrowsInfo(listOf(ex)))
        statusCfg.exception<EX> { t ->
            val ret = gen(t)
            if (ret != null) {
                call.respond(status, ret)
            } else {
                call.respond(status)
            }
        }
    }
}
