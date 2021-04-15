package com.papsign.ktor.openapigen

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface APIException<TException : Throwable, TMessage> {
    val status: HttpStatusCode
    val exceptionClass: KClass<TException>
    val contentType: KType
        get() = unitKType
    val contentFn: ((TException) -> TMessage)?
        get() = null
    val example: TMessage?
        get() = null

    /**
     * @param status The HTTP status code to return
     * @param exceptionClass The Kclass of the exception
     * @param example An example of the HTTP response
     * @param contentType The media type of the HTTP response
     * @param contentFn The function that creates the HTTP response
     */
    class APIExceptionImpl<TException : Throwable, TMessage>(
        override val status: HttpStatusCode,
        override val exceptionClass: KClass<TException>,
        override val contentType: KType = unitKType,
        override val contentFn: ((TException) -> TMessage)? = null,
        override val example: TMessage? = null
    ) : APIException<TException, TMessage>

    companion object {
        inline fun <reified TException : Throwable> apiException(status: HttpStatusCode):
                APIException<TException, Unit> = apiException(status, null as Unit?, null)

        inline fun <reified TException : Throwable, reified TMessage> apiException(
            status: HttpStatusCode,
            example: TMessage? = null,
            noinline contentFn: ((TException) -> TMessage)? = null
        ): APIException<TException, TMessage> =
            APIExceptionImpl(
                status = status,
                exceptionClass = TException::class,
                contentType = getKType<TMessage>(),
                contentFn = contentFn,
                example = example
            )
    }

    class APIExceptionBuilder<TException : Throwable, TMessage> {
        var status: HttpStatusCode = HttpStatusCode.BadRequest
        var example: TMessage? = null
        var contentFn: ((TException) -> TMessage)? = null

        companion object {
            inline fun <reified TException : Throwable, reified TMessage> apiException(
                block: APIExceptionBuilder<TException, TMessage>.() -> Unit
            ) : APIException<TException, TMessage> =
                APIExceptionBuilder<TException, TMessage>().run {
                    block(this)
                    apiException(status, example, contentFn)
                }
        }
    }

}
