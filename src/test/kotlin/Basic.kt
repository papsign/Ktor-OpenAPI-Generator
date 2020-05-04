import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.reflect.KType

object Basic {

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(Netty, 8080, "localhost") {
            //define basic OpenAPI info
            install(OpenAPIGen) {
                // basic info
                info {
                    version = "0.0.1"
                    title = "Test API"
                    description = "The Test API"
                    contact {
                        name = "Support"
                        email = "support@test.com"
                    }
                }
                // describe the server, add as many as you want
                server("http://localhost:8080/") {
                    description = "Test server"
                }
                //optional
                replaceModule(DefaultSchemaNamer, object: SchemaNamer {
                    val regex = Regex("[A-Za-z0-9_.]+")
                    override fun get(type: KType): String {
                       return type.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
                    }
                })
            }

            install(ContentNegotiation) {
                jackson()
            }

            // normal Ktor routing
            routing {
                get("/openapi.json") {
                    call.respond(application.openAPIGen.api.serialize())
                }

                get("/") {
                    call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
                }
            }

            //Described routing
            apiRouting {

                //bare minimum, just like Ktor but strongly typed
                get<StringParam, StringParam> { params ->
                    respond(params)
                }

                route("inine").get<StringParam, StringResponse>(
                    info("String Param Endpoint", "This is a String Param Endpoint"), // A Route module that describes an endpoint, it is optional
                    example = StringResponse("Hi")
                ) { params ->
                    respond(StringResponse(params.a))
                }

                route("block") {
                    // use Unit if there are no parameters / body / response
                    post<Unit, StringUsable,  Set<StringUsable>>(
                        info("String Post Endpoint", "This is a String Post Endpoint"),
                        exampleRequest = setOf(StringUsable("Ho")),
                        exampleResponse = StringUsable("Ho")
                    ) { params, body ->
                        respond(body.first())
                    }
                }

                route("generic") {
                    post<Unit, GenericTest<A?>, GenericTest<A?>> { params, body ->
                        respond(body)
                    }
                }
            }
        }.start(true)

    }

    // Path works like the @Location from locations, but for transparency we recommend only using it to extract the parameters
    @Path("string/{a}")
    data class StringParam(
        @PathParam("A simple String Param", style = PathParamStyle.matrix) val a: String,
        @QueryParam("Optional String") val optional: A? // Nullable Types are optional
    )

    data class A(val b: String)

    @Request
    data class GenericTest<T>(val value: T)

    // A response can be any class, but a description will be generated from the annotation
    @Response("A String Response")
    data class StringResponse(val str: String)


    // DTOs can be requests and responses, annotations are optional
    @Response("A String Response")
    @Request("A String Request")
    data class StringUsable(val str: String)
}
