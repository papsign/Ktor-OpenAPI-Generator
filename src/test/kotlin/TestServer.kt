import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.papsign.ktor.openapigen.APITag
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.interop.OAuth2Handler
import com.papsign.ktor.openapigen.interop.configure
import com.papsign.ktor.openapigen.interop.withAPI
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.openapi.Described
import com.papsign.ktor.openapigen.openapi.Server
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.auth.auth
import com.papsign.ktor.openapigen.route.path.auth.get
import com.papsign.ktor.openapigen.route.path.auth.post
import com.papsign.ktor.openapigen.route.path.auth.principal
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.tag
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.OAuthServerSettings
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.origin
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

object TestServer {

    data class Error(val id: String, val msg: String)

    class ProperException(msg: String, val id: String = "proper.exception") : Exception(msg)

    lateinit var oauth: OAuth2Handler<APIPrincipal, Scopes>

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(Netty, 8080, "localhost") {
            //define basic OpenAPI info
            val api = install(OpenAPIGen) {
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
                schemaNamer = {
                    //rename DTOs from java type name to generator compatible form
                    val regex = Regex("[A-Za-z0-9_.]+")
                    it.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
                }
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

            // StatusPage interop, can also define exceptions per-route
            install(StatusPages) {
                withAPI(api) {
                    exception<JsonMappingException, Error>(HttpStatusCode.BadRequest) {
                        it.printStackTrace()
                        Error("mapping.json", it.localizedMessage)
                    }
                    exception<ProperException, Error>(HttpStatusCode.BadRequest) {
                        it.printStackTrace()
                        Error(it.id, it.localizedMessage)
                    }
                }
            }


            val scopes = Scopes.values().asList()

            val googleOAuthProvider = OAuthServerSettings.OAuth2ServerSettings(
                name = "google",
                authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                accessTokenUrl = "https://www.googleapis.com/oauth2/v3/token",
                requestMethod = HttpMethod.Post,

                clientId = "<id>",
                clientSecret = "<secret>",
                defaultScopes = scopes.map { it.name }
            )

            oauth = OAuth2Handler(googleOAuthProvider, scopes, scopes, scopes, scopes) { auth ->
                APIPrincipal(auth.tokenType, auth.accessToken)
            }

            // auth interop
            install(Authentication) {
                configure(oauth)
            }

            // serve OpenAPI and redirect from root
            routing {
                get("/openapi.json") {
                    val host = Server(
                        call.request.origin.scheme + "://" + call.request.host() + if (setOf(
                                80,
                                443
                            ).contains(call.request.port())
                        ) "" else ":${call.request.port()}"
                    )
                    application.openAPIGen.api.servers.add(0, host)
                    call.respond(application.openAPIGen.api)
                    application.openAPIGen.api.servers.remove(host)
                }

                get("/") {
                    call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
                }
            }

            apiRouting {

                get<StringParam, StringResponse>(
                    info("String Param Endpoint", "This is a String Param Endpoint"),
                    example = StringResponse("Hi")
                ) { params ->
                    respond(StringResponse(params.a))
                }

                route("long").get<LongParam, LongResponse>(
                    info("Long Param Endpoint", "This is a String Param Endpoint"),
                    example = LongResponse(Long.MAX_VALUE)
                ) { params ->
                    respond(LongResponse(params.a))
                }

                route("again") {
                    tag(TestServer.Tags.EXAMPLE) {

                        get<StringParam, StringResponse>(
                            info("String Param Endpoint", "This is a String Param Endpoint"),
                            example = StringResponse("Hi")
                        ) { params ->
                            respond(StringResponse(params.a))
                        }

                        route("long").get<LongParam, LongResponse>(
                            info("Long Param Endpoint", "This is a String Param Endpoint"),
                            example = LongResponse(Long.MAX_VALUE)
                        ) { params ->
                            respond(LongResponse(params.a))
                        }
                    }

                }

                tag(TestServer.Tags.EXAMPLE) {
                    route("authenticated") {

                        auth(oauth) {

                            get<StringParam, StringResponse, APIPrincipal>(
                                info(
                                    "Authenticated String Param Endpoint",
                                    "This is aa authenticated String Param Endpoint"
                                ),
                                example = StringResponse("Hi")
                            ) { params ->
                                val p = principal()
                                respond(StringResponse(params.a + p.a + p.b))
                            }


                            post<Unit, StringUsable, StringUsable, APIPrincipal> { _, str ->
                                respond(str)
                            }

                        }
                    }
                }
            }
        }.start(true)
    }

    @Path("string/{a}")
    data class StringParam(@PathParam("A simple String Param") val a: String)

    @Response("A String Response")
    data class StringResponse(val str: String)


    @Response("A String Response")
    @Request("A String Request")
    data class StringUsable(val str: String)

    @Path("{a}")
    data class LongParam(@PathParam("A simple Long Param") val a: Long)

    @Response("A Long Response")
    data class LongResponse(val str: Long)


    enum class Tags(override val description: String): APITag {
        EXAMPLE("Wow this is a tag?!")
    }

    enum class Scopes(override val description: String): Described {
        profile("Basic Profile scope"), email("Email scope")
    }

    data class APIPrincipal(val a: String, val b: String)
}
