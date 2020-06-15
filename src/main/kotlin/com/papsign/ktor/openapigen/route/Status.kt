package com.papsign.ktor.openapigen.route

import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.modules.RouteOpenAPIModule
import com.papsign.ktor.openapigen.modules.providers.StatusProvider
import com.papsign.ktor.openapigen.modules.registerModule
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 *  Sets the success response status code of the endpoint
 */
fun status(code: HttpStatusCode) = StatusCode(code)

/**
 *  Sets the success response status code of the endpoint
 */
fun status(code: Int) = StatusCode(HttpStatusCode.fromValue(code))

/**
 *  Success response status code of the endpoint will be derived from the @Response annotation
 */
fun responseAnnotationStatus() = ResponseAnnotationStatusCode

/**
 *  Sets the success response status code of the endpoints defined in the block
 */
inline fun <T: OpenAPIRoute<T>> T.status(code: HttpStatusCode, crossinline fn: T.() -> Unit) {
    child().apply { provider.registerModule(status(code)) }.fn()
}

/**
 *  Sets the success response status code of the endpoints defined in the block
 */
inline fun <T: OpenAPIRoute<T>> T.status(code: Int, crossinline fn: T.() -> Unit) {
    child().apply { provider.registerModule(status(code)) }.fn()
}

/**
 * Success response status code of the endpoints defined in the block will be derived from the @Response annotation
 */
inline fun <T: OpenAPIRoute<T>> T.responseAnnotationStatus(crossinline fn: T.() -> Unit) {
    child().apply { provider.registerModule(responseAnnotationStatus()) }.fn()
}

data class StatusCode(val code: HttpStatusCode) : StatusProvider, RouteOpenAPIModule {
    override fun getStatusForType(responseType: KType): HttpStatusCode = code
}

object ResponseAnnotationStatusCode : StatusProvider, RouteOpenAPIModule {
    override fun getStatusForType(responseType: KType): HttpStatusCode {
        return (responseType.classifier as? KAnnotatedElement)?.findAnnotation<Response>()?.statusCode?.let { HttpStatusCode.fromValue(it) } ?: HttpStatusCode.OK
    }
}
