package com.papsign.ktor.openapigen

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface APIException<EX : Throwable, B> {
    val status: HttpStatusCode
    val exceptionClass: KClass<EX>
    val contentType: KType
        get() = unitKType
    val contentGen: ((EX) -> B)?
        get() = null
    val example: B?
        get() = null

    companion object {
        class APIExceptionProxy<EX : Throwable, B>(
            override val status: HttpStatusCode,
            override val exceptionClass: KClass<EX>,
            override val example: B? = null,
            override val contentType: KType = unitKType,
            override val contentGen: ((EX) -> B)? = null
        ) : APIException<EX, B>

        inline fun <reified EX : Throwable> apiException(status: HttpStatusCode): APIException<EX, Unit> {
            return apiException(status, null as Unit?)
        }

        inline fun <reified EX : Throwable, reified B> apiException(
            status: HttpStatusCode,
            example: B? = null,
            noinline gen: ((EX) -> B)? = null
        ): APIException<EX, B> {
            return APIExceptionProxy(
                status, EX::class,
                example,
                getKType<B>(), gen
            )
        }
    }
}
