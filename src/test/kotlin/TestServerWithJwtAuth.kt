import io.ktor.auth.jwt.jwt
import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.properties.description.Description
import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.security.HttpSecurityScheme
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeType
import com.papsign.ktor.openapigen.model.server.ServerModel
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.*
import com.papsign.ktor.openapigen.route.path.auth.OpenAPIAuthenticatedRoute
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.auth.*
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.Principal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.features.ContentNegotiation
import io.ktor.features.origin
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.PipelineContext
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.reflect.KType

object TestServerWithJwtAuth {

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(Netty, 8080, "localhost") {
            testServerWithJwtAuth()
        }.start(true)
    }

    fun Application.testServerWithJwtAuth() {
        //define basic OpenAPI info
        val authProvider = JwtProvider()
        install(OpenAPIGen) {
            info {
                version = "0.1"
                title = "Test API"
                description = "The Test API"
                contact {
                    name = "Support"
                    email = "support@test.com"
                }
            }
            server("https://api.test.com/") {
                description = "Main production server"
            }
            addModules(authProvider)
            replaceModule(DefaultSchemaNamer, object: SchemaNamer {
                val regex = Regex("[A-Za-z0-9_.]+")
                override fun get(type: KType): String {
                    return type.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
                }
            })
        }

        install(ContentNegotiation) {
            jackson {
                enable(
                    DeserializationFeature.WRAP_EXCEPTIONS,
                    DeserializationFeature.USE_BIG_INTEGER_FOR_INTS,
                    DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS
                )

                enable(SerializationFeature.WRAP_EXCEPTIONS, SerializationFeature.INDENT_OUTPUT)

                setSerializationInclusion(JsonInclude.Include.NON_NULL)

                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })

                registerModule(JavaTimeModule())
            }
        }

        install(Authentication) {
            installJwt(this)
        }

        // serve OpenAPI and redirect from root
        routing {
            get("/openapi.json") {
                val host = ServerModel(
                    call.request.origin.scheme + "://" + call.request.host() + if (setOf(
                            80,
                            443
                        ).contains(call.request.port())
                    ) "" else ":${call.request.port()}"
                )
                application.openAPIGen.api.servers.add(0, host)
                call.respond(application.openAPIGen.api.serialize())
                application.openAPIGen.api.servers.remove(host)
            }

            get("/") {
                call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
            }
        }

        apiRouting {
            auth {
                get<StringParam, StringResponse, UserPrincipal>(
                    info("String Param Endpoint", "This is a String Param Endpoint"),
                    example = StringResponse("Hi")
                ) { params ->
                    val (_, name) = principal()
                    respond(StringResponse("Hello $name, you submitted ${params.a}"))
                }
            }
        }
    }

    @Path("string/{a}")
    data class StringParam(@PathParam("A simple String Param") val a: String)

    @Response("A String Response")
    data class StringResponse(@Description("The string value") val str: String)

    private val authProvider = JwtProvider()

    private inline fun NormalOpenAPIRoute.auth(route: OpenAPIAuthenticatedRoute<UserPrincipal>.() -> Unit): OpenAPIAuthenticatedRoute<UserPrincipal> {
        val authenticatedKtorRoute = this.ktorRoute.authenticate { }
        val openAPIAuthenticatedRoute= OpenAPIAuthenticatedRoute(authenticatedKtorRoute, this.provider.child(), authProvider = authProvider)
        return openAPIAuthenticatedRoute.apply {
            route()
        }
    }

    data class UserPrincipal(val userId: String, val name: String?) : Principal

    class JwtProvider : AuthProvider<UserPrincipal> {
        override val security: Iterable<Iterable<AuthProvider.Security<*>>> =
            listOf(listOf(
                AuthProvider.Security(
                    SecuritySchemeModel(
                        SecuritySchemeType.http,
                        scheme = HttpSecurityScheme.bearer,
                        bearerFormat = "JWT",
                        name = "jwtAuth"
                    ), emptyList<Scopes>()
                )
            ))

        override suspend fun getAuth(pipeline: PipelineContext<Unit, ApplicationCall>): UserPrincipal {
            return pipeline.context.authentication.principal() ?: throw RuntimeException("No JWTPrincipal")
        }

        override fun apply(route: NormalOpenAPIRoute): OpenAPIAuthenticatedRoute<UserPrincipal> {
            val authenticatedKtorRoute = route.ktorRoute.authenticate { }
            return OpenAPIAuthenticatedRoute(authenticatedKtorRoute, route.provider.child(), this)
        }
    }

    enum class Scopes(override val description: String) : Described {
        Profile("Some scope")
    }

    private const val jwtRealm = "example-jwt-realm"
    private const val jwtIssuer = "http://localhost:9091/auth/realms/$jwtRealm"
    private const val jwtEndpoint = "$jwtIssuer/protocol/openid-connect/certs"

    private fun installJwt (provider: Authentication.Configuration) {
        provider.apply {
            jwt {
                realm = jwtRealm
                verifier(getJwkProvider(jwtEndpoint), jwtIssuer)
                validate { credentials ->
                    UserPrincipal(
                        credentials.payload.subject,
                        credentials.payload.claims["name"]?.asString())
                }
            }
        }
    }

    private fun getJwkProvider(jwkEndpoint: String): JwkProvider {
        return JwkProviderBuilder(URL(jwkEndpoint))
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
    }

}
