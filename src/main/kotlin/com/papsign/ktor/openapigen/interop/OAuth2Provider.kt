package com.papsign.ktor.openapigen.interop

import com.papsign.ktor.openapigen.APIException
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.openapi.Described
import com.papsign.ktor.openapigen.openapi.Flows
import com.papsign.ktor.openapigen.openapi.SecurityScheme
import com.papsign.ktor.openapigen.openapi.SecuritySchemeType
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.throws
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.origin
import io.ktor.http.HttpStatusCode
import io.ktor.request.host
import io.ktor.request.path
import io.ktor.request.port
import io.ktor.util.pipeline.PipelineContext


class OAuth2Handler<A, T>(
    val settings: OAuthServerSettings.OAuth2ServerSettings,
    private val implicitScopes: List<T>? = null,
    private val passwordScopes: List<T>? = null,
    private val clientCredentialsScopes: List<T>? = null,
    private val authorizationCodeScopes: List<T>? = null,
    val httpClient: HttpClient = HttpClient(Apache),
    val urlProvider: ApplicationCall.(OAuthServerSettings) -> String = { defaultURLProvider(it) },
    private val auth: suspend (principal: OAuthAccessTokenResponse.OAuth2) -> A?
) where T : Enum<T>, T : Described {
    val authName = settings.name + "-oauth2-openapi"

    private class BadPrincipalException : Exception("Could not get principal from token")

    suspend fun getAuth(principal: OAuthAccessTokenResponse.OAuth2): A =
        auth(principal) ?: throw BadPrincipalException()

    private val flows = Flows<T>().apply {
        if (implicitScopes != null) implicit(
            implicitScopes, settings.authorizeUrl,
            settings.accessTokenUrl
        )
        if (passwordScopes != null) password(
            passwordScopes, settings.accessTokenUrl,
            settings.accessTokenUrl
        )
        if (clientCredentialsScopes != null) clientCredentials(
            clientCredentialsScopes, settings.accessTokenUrl,
            settings.accessTokenUrl
        )
        if (authorizationCodeScopes != null) authorizationCode(
            authorizationCodeScopes,
            settings.authorizeUrl,
            settings.accessTokenUrl,
            settings.accessTokenUrl
        )
    }

    val scheme = SecurityScheme(SecuritySchemeType.oauth2, settings.name, flows = flows)

    private inner class OAuth2Provider(scopes: List<T>) : AuthProvider<A> {
        override suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): A =
            this@OAuth2Handler.getAuth(pipeline.call.principal()!!)

        override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<A> =
            OpenAPIAuthenticatedRoute(route.ktorRoute.authenticate(authName) {}, route.provider.child(), this).throws(
                APIException.apiException<BadPrincipalException>(HttpStatusCode.Unauthorized)
            )

        override val security: Iterable<Iterable<AuthProvider.Security<*>>> =
            listOf(listOf(AuthProvider.Security(scheme, scopes)))
    }


    fun auth(apiRoute: NormalOpenAPIRoute, scopes: List<T>): OpenAPIAuthenticatedRoute<A> {
        val authProvider = OAuth2Provider(scopes)
        return authProvider.apply(apiRoute)
    }

    companion object {
        fun ApplicationCall.defaultURLProvider(settings: OAuthServerSettings): String {
            val defaultPort = if (request.origin.scheme == "http") 80 else 443
            val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
            val protocol = request.origin.scheme
            val uri = request.path()
            return "$protocol://$hostPort$uri"
        }
    }
}

fun Authentication.Configuration.configure(handler: OAuth2Handler<*, *>) {
    oauth(handler.authName) {
        client = handler.httpClient
        providerLookup = { handler.settings }
        urlProvider = handler.urlProvider
    }
}
