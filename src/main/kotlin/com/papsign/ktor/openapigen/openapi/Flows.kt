package com.papsign.ktor.openapigen.openapi

import java.util.*
import kotlin.reflect.KProperty

class Flows<T>: MutableMap<String, Flows.Flow<T>> by HashMap<String, Flow<T>>()
        where T : Enum<T>, T : Described {

    private var implicit: Flow<T>? by this
    private var password: Flow<T>? by this
    private var clientCredentials: Flow<T>? by this
    private var authorizationCode: Flow<T>? by this

    fun implicit(
        scopes: Iterable<T>,
        authorizationUrl: String,
        refreshUrl: String? = null
    ): Flows<T> {
        implicit = Flow.implicit(scopes, authorizationUrl, refreshUrl)
        return this
    }

    fun password(
        scopes: Iterable<T>, tokenUrl: String, refreshUrl: String? = null
    ): Flows<T> {
        password = Flow.password(scopes, tokenUrl, refreshUrl)
        return this
    }

    fun clientCredentials(
        scopes: Iterable<T>,
        tokenUrl: String,
        refreshUrl: String? = null
    ): Flows<T> {
        clientCredentials =
            Flow.clientCredentials(scopes, tokenUrl, refreshUrl)
        return this
    }

    fun authorizationCode(
        scopes: Iterable<T>,
        authorizationUrl: String,
        tokenUrl: String,
        refreshUrl: String? = null
    ): Flows<T> {
        authorizationCode = Flow.authorizationCode(
            scopes,
            authorizationUrl,
            tokenUrl,
            refreshUrl
        )
        return this
    }

    private operator fun setValue(any: Any, property: KProperty<*>, any1: Flow<T>?) {
        if (any1 == null)
            this.remove(property.name)
        else
            this[property.name] = any1
    }

    private operator fun getValue(any: Any, property: KProperty<*>): Flow<T>? {
        return this[property.name]
    }

    companion object {
        private val IMPLICIT = "implicit"
        private val PASSWORD = "password"
        private val AUTHORIZATION_CODE = "authorization_code"
        private val CLIENT_CREDENTIALS = "client_credentials"
        private val REFRESH_TOKEN = "refresh_token"
    }

    enum class FlowType(val value: String) {
        implicit(IMPLICIT), password(PASSWORD), client_credentials(CLIENT_CREDENTIALS), authorization_code(AUTHORIZATION_CODE), refresh_token(REFRESH_TOKEN);
    }


    class Flow<T> private constructor(
        val authorizationUrl: String? = null,
        val tokenUrl: String? = null,
        val refreshUrl: String? = null,
        val scopes: Map<T, String>
    ) where T : Enum<T>, T : Described {
        companion object {
            fun <T> implicit(
                scopes: Iterable<T>,
                authorizationUrl: String,
                refreshUrl: String? = null
            ): Flow<T> where T : Enum<T>, T : Described {
                return Flow(authorizationUrl, null, refreshUrl, scopes.associateWith { it.description })
            }

            fun <T> password(
                scopes: Iterable<T>, tokenUrl: String, refreshUrl: String? = null
            ): Flow<T> where T : Enum<T>, T : Described {
                return Flow(null, tokenUrl, refreshUrl, scopes.associateWith { it.description })
            }

            fun <T> clientCredentials(
                scopes: Iterable<T>,
                tokenUrl: String,
                refreshUrl: String? = null
            ): Flow<T> where T : Enum<T>, T : Described {
                return Flow(null, tokenUrl, refreshUrl, scopes.associateWith { it.description })
            }

            fun <T> authorizationCode(
                scopes: Iterable<T>,
                authorizationUrl: String,
                tokenUrl: String,
                refreshUrl: String? = null
            ): Flow<T> where T : Enum<T>, T : Described {
                return Flow(authorizationUrl, tokenUrl, refreshUrl, scopes.associateWith { it.description })
            }
        }
    }
}
