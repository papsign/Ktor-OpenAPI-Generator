package com.papsign.ktor.openapigen

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface APIException<EX: Throwable, B> {
    val status: HttpStatusCode
    val exceptionClass: KClass<EX>
    val contentType: KType
        get() = unitKType
    val contentGen: ((EX)->B)?
        get() = null

    companion object {
        class APIExceptionProxy<EX: Throwable, B>(override val status: HttpStatusCode,
                                                  override val exceptionClass: KClass<EX>,
                                                  override val contentType: KType = unitKType,
                                                  override val contentGen: ((EX)->B)? = null): APIException<EX, B>

        class EmptyAPIExceptionProxy<EX: Throwable>(override val status: HttpStatusCode,
                                                    override val exceptionClass: KClass<EX>): APIException<EX, Unit>


        inline fun <reified EX: Throwable> apiException(status: HttpStatusCode): APIException<EX, Unit> {
            return EmptyAPIExceptionProxy(status, EX::class)
        }

        inline fun <reified EX: Throwable, reified B> apiException(status: HttpStatusCode, noinline gen: (EX)->B): APIException<EX, B> {
            return APIExceptionProxy(status, EX::class,
                getKType<B>(), gen)
        }
    }
}
