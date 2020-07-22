package com.papsign.ktor.openapigen.model.security

import com.papsign.ktor.openapigen.annotations.mapping.openAPIName
import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.Described
import java.util.*
import kotlin.reflect.KProperty

class FlowsModel<TScope> : MutableMap<String, FlowsModel.FlowModel<TScope>> by HashMap<String, FlowModel<TScope>>()
        where TScope : Enum<TScope>, TScope : Described {

    private var implicit: FlowModel<TScope>? by this
    private var password: FlowModel<TScope>? by this
    private var clientCredentials: FlowModel<TScope>? by this
    private var authorizationCode: FlowModel<TScope>? by this

    fun implicit(
        scopes: Iterable<TScope>,
        authorizationUrl: String,
        refreshUrl: String? = null
    ): FlowsModel<TScope> {
        implicit =
            FlowModel.implicit(
                scopes,
                authorizationUrl,
                refreshUrl
            )
        return this
    }

    fun password(
        scopes: Iterable<TScope>, tokenUrl: String, refreshUrl: String? = null
    ): FlowsModel<TScope> {
        password =
            FlowModel.password(
                scopes,
                tokenUrl,
                refreshUrl
            )
        return this
    }

    fun clientCredentials(
        scopes: Iterable<TScope>,
        tokenUrl: String,
        refreshUrl: String? = null
    ): FlowsModel<TScope> {
        clientCredentials =
            FlowModel.clientCredentials(
                scopes,
                tokenUrl,
                refreshUrl
            )
        return this
    }

    fun authorizationCode(
        scopes: Iterable<TScope>,
        authorizationUrl: String,
        tokenUrl: String,
        refreshUrl: String? = null
    ): FlowsModel<TScope> {
        authorizationCode =
            FlowModel.authorizationCode(
                scopes,
                authorizationUrl,
                tokenUrl,
                refreshUrl
            )
        return this
    }

    private operator fun setValue(any: Any, property: KProperty<*>, any1: FlowModel<TScope>?) {
        if (any1 == null)
            this.remove(property.name)
        else
            this[property.name] = any1
    }

    private operator fun getValue(any: Any, property: KProperty<*>): FlowModel<TScope>? {
        return this[property.name]
    }

    companion object {
        private val IMPLICIT = "implicit"
        private val PASSWORD = "password"
        private val AUTHORIZATION_CODE = "authorization_code"
        private val CLIENT_CREDENTIALS = "client_credentials"
        private val REFRESH_TOKEN = "refresh_token"
    }

    enum class FlowModelType(val value: String) {
        implicit(IMPLICIT),
        password(PASSWORD),
        client_credentials(CLIENT_CREDENTIALS),
        authorization_code(AUTHORIZATION_CODE),
        refresh_token(REFRESH_TOKEN);
    }


    class FlowModel<T> private constructor(
        val authorizationUrl: String? = null,
        val tokenUrl: String? = null,
        val refreshUrl: String? = null,
        val scopes: Map<T, String>
    ) : DataModel where T : Enum<T>, T : Described {
        companion object {
            fun <T> implicit(
                scopes: Iterable<T>,
                authorizationUrl: String,
                refreshUrl: String? = null
            ): FlowModel<T> where T : Enum<T>, T : Described {
                return FlowModel(
                    authorizationUrl,
                    null,
                    refreshUrl,
                    scopes.associateWith { it.description })
            }

            fun <T> password(
                scopes: Iterable<T>, tokenUrl: String, refreshUrl: String? = null
            ): FlowModel<T> where T : Enum<T>, T : Described {
                return FlowModel(
                    null,
                    tokenUrl,
                    refreshUrl,
                    scopes.associateWith { it.description })
            }

            fun <T> clientCredentials(
                scopes: Iterable<T>,
                tokenUrl: String,
                refreshUrl: String? = null
            ): FlowModel<T> where T : Enum<T>, T : Described {
                return FlowModel(
                    null,
                    tokenUrl,
                    refreshUrl,
                    scopes.associateWith { it.description })
            }

            fun <T> authorizationCode(
                scopes: Iterable<T>,
                authorizationUrl: String,
                tokenUrl: String,
                refreshUrl: String? = null
            ): FlowModel<T> where T : Enum<T>, T : Described {
                return FlowModel(
                    authorizationUrl,
                    tokenUrl,
                    refreshUrl,
                    scopes.associateWith { it.description })
            }
        }
    }
}
