package com.papsign.ktor.openapigen

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface APIException<EX : Throwable, B> {
    val status: HttpStatusCode
    val exceptionClass: KClass<EX>
    val contentType: KType
        get() = unitKType
    val contentFn: ((EX) -> B)?
        get() = null
    val example: B?
        get() = null

    /**
     * @param status The HTTP status code to return
     * @param exceptionClass The Kclass of the exception
     * @param example An example of the HTTP response
     * @param contentType The media type of the HTTP response
     * @param contentFn The function that creates the HTTP response
     */
    class APIExceptionImpl<EX : Throwable, B>(
        override val status: HttpStatusCode,
        override val exceptionClass: KClass<EX>,
        override val contentType: KType = unitKType,
        override val contentFn: ((EX) -> B)? = null,
        override val example: B? = null
    ) : APIException<EX, B>

    companion object {
        inline fun <reified EX : Throwable> apiException(status: HttpStatusCode): APIException<EX, Unit> =
            apiException(status, null as Unit?, null)

        inline fun <reified EX : Throwable, reified B> apiException(
            status: HttpStatusCode,
            example: B? = null,
            noinline contentFn: ((EX) -> B)? = null
        ): APIException<EX, B> = APIExceptionImpl(
            status = status,
            exceptionClass = EX::class,
            contentType = getKType<B>(),
            contentFn = contentFn,
            example = example,
        )
    }
}
