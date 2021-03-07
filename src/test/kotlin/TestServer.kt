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
import com.papsign.ktor.openapigen.annotations.mapping.OpenAPIName
import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.annotations.properties.description.Description
import com.papsign.ktor.openapigen.annotations.type.common.ConstraintViolation
import com.papsign.ktor.openapigen.annotations.type.number.floating.clamp.FClamp
import com.papsign.ktor.openapigen.annotations.type.number.floating.max.FMax
import com.papsign.ktor.openapigen.annotations.type.number.integer.clamp.Clamp
import com.papsign.ktor.openapigen.annotations.type.number.integer.min.Min
import com.papsign.ktor.openapigen.annotations.type.string.example.StringExample
import com.papsign.ktor.openapigen.annotations.type.string.length.Length
import com.papsign.ktor.openapigen.annotations.type.string.length.MaxLength
import com.papsign.ktor.openapigen.annotations.type.string.length.MinLength
import com.papsign.ktor.openapigen.annotations.type.string.pattern.RegularExpression
import com.papsign.ktor.openapigen.interop.withAPI
import com.papsign.ktor.openapigen.model.Described
import com.papsign.ktor.openapigen.model.server.ServerModel
import com.papsign.ktor.openapigen.openAPIGen
import com.papsign.ktor.openapigen.route.*
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.time.*
import kotlin.reflect.KType

object TestServer {

    data class Error(val id: String, val msg: String)

    class ProperException(msg: String, val id: String = "proper.exception") : Exception(msg)

    fun Application.testServer() {
        Setup()

        apiRouting {

            get<StringParam, StringResponse>(
                info("String Param Endpoint", "This is a String Param Endpoint"),
                example = StringResponse("Hi")
            ) { params ->
                respond(StringResponse(params.a))
            }

            route("header") {
                get<NameParam, NameGreetingResponse>(
                    info("Header Param Endpoint", "This is a Header Param Endpoint"),
                    example = NameGreetingResponse("Hi, openapi!")
                ) { params ->
                    respond(NameGreetingResponse("Hi, ${params.name}!"))
                }
            }

            route("list") {
                get<StringParam, List<StringResponse>>(
                    info("String Param Endpoint", "This is a String Param Endpoint"),
                    example = listOf(StringResponse("Hi"))
                ) { params ->
                    respond(listOf(StringResponse(params.a)))
                }
            }

            SealedRoute()

            route("long").get<LongParam, LongResponse>(
                info("Long Param Endpoint", "This is a String Param Endpoint"),
                example = LongResponse(Long.MAX_VALUE)
            ) { params ->
                respond(LongResponse(params.a))
            }

            route("validate-string").post<Unit, StringResponse, StringValidatorsExample>(
                info(
                    "This endpoint demonstrates the usage of String validators",
                    "This endpoint demonstrates the usage of String validators"
                ),
                exampleRequest = StringValidatorsExample(
                    "A string that is at least 2 characters long",
                    "A short string",
                    "Between 2 and 20",
                    "5a21be2"
                ),
                exampleResponse = StringResponse("All of the fields were valid")
            ) { params, body ->
                respond(StringResponse("All of the fields were valid"))
            }

            route("validate-number").post<Unit, StringResponse, NumberValidatorsExample>(
                info(
                    "This endpoint demonstrates the usage of number validators",
                    "This endpoint demonstrates the usage of number validators"
                ),
                exampleRequest = NumberValidatorsExample(
                    1,
                    56,
                    15.02f,
                    0.023f
                ),
                exampleResponse = StringResponse("All of the fields were valid")
            ) { params, body ->
                respond(StringResponse("All of the fields were valid"))
            }

            route("status/codes") {
                route("201").status(201) {
                    // all endpoints in this block respond a 201 status code unless specified otherwise

                    get<StringParam, StringResponse>(
                        info(
                            "201 String Param Endpoint",
                            "This is a String Param Endpoint that has a 201 status code"
                        ),
                        example = StringResponse("Hi")
                    ) { params ->
                        respond(StringResponse(params.a))
                    }

                    route("reset").get<StringParam, StringResponse>(
                        info(
                            "String Param Endpoint with @response based status code",
                            "This is a String Param Endpoint that resets the status code back to the one provided by @Response"
                        ),
                        responseAnnotationStatus(),
                        example = StringResponse("Hi")
                    ) { params ->
                        respond(StringResponse(params.a))
                    }
                }

                route("202").get<StringParam, StringResponse>(
                    info(
                        "String Param Endpoint with inline 202 response",
                        "This is a String Param Endpoint that has a 202 response code"
                    ),
                    status(HttpStatusCode.Accepted),
                    example = StringResponse("Hi")
                ) { params ->
                    respond(StringResponse(params.a))
                }
            }

            route("again") {
                tag(Tags.EXAMPLE) {

                    route("exception").throws(HttpStatusCode.ExpectationFailed, "example", CustomException::class) {
                        get<StringParam, StringResponse>(
                            info("String Param Endpoint", "This is a String Param Endpoint"),
                            example = StringResponse("Hi")
                        ) { params ->
                            throw CustomException()
                        }
                    }

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


            route("datetime") {
                route("date") {
                    get<LocalDateQuery, LocalDateResponse> { params ->
                        respond(LocalDateResponse(params.date))
                    }
                    route("optional") {
                        get<LocalDateOptionalQuery, LocalDateResponse> { params ->
                            println(params)
                            respond(LocalDateResponse(params.date))
                        }
                    }
                }
                route("local-time") {
                    get<LocalTimeQuery, LocalTimeResponse> { params ->
                        respond(LocalTimeResponse(params.time))
                    }
                }
                route("offset-time") {
                    get<OffsetTimeQuery, OffsetTimeResponse> { params ->
                        respond(OffsetTimeResponse(params.time))
                    }
                }

                route("local-date-time") {
                    get<LocalDateTimeQuery, LocalDateTimeResponse> { params ->
                        respond(LocalDateTimeResponse(params.date))
                    }
                }
                route("offset-date-time") {
                    get<OffsetDateTimeQuery, OffsetDateTimeResponse> { params ->
                        respond(OffsetDateTimeResponse(params.date))
                    }
                }
                route("zoned-date-time") {
                    get<ZonedDateTimeQuery, ZonedDateTimeResponse> { params ->
                        println(ZonedDateTime.now())
                        respond(ZonedDateTimeResponse(params.date))
                    }
                }
                route("instant") {
                    get<InstantQuery, InstantResponse> { params ->
                        respond(InstantResponse(params.date))
                    }
                }
            }
        }
    }


    fun Application.Setup() {
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
            replaceModule(DefaultSchemaNamer, object : SchemaNamer {
                val regex = Regex("[A-Za-z0-9_.]+")
                override fun get(type: KType): String {
                    return type.toString().replace(regex) { it.value.split(".").last() }
                        .replace(Regex(">|<|, "), "_")
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

                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)

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
                exception<ConstraintViolation, Error>(HttpStatusCode.BadRequest) {
                    Error("violation.constraint", it.localizedMessage)
                }
                exception<ProperException, Error>(HttpStatusCode.BadRequest) {
                    it.printStackTrace()
                    Error(it.id, it.localizedMessage)
                }
            }
        }

        val scopes = Scopes.values().asList()

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
    }

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(Netty, 8080, "localhost") { testServer() }.start(true)
    }

    class CustomException : Exception()

    @Path("string/{a}")
    data class StringParam(@PathParam("A simple String Param") val a: String)

    @Response("A String Response")
    data class StringResponse(@Description("The string value") val str: String)

    data class NameParam(@HeaderParam("A simple Header Param") @OpenAPIName("X-NAME") val name: String)

    @Response("A Response for header param example")
    data class NameGreetingResponse(@StringExample("Hi, John!") val str: String)

    @Request("A Request with String fields validated for length or pattern")
    data class StringValidatorsExample(
        @MinLength(2, "Optional custom error message") val strWithMinLength: String,
        @MaxLength(20) val strWithMaxLength: String,
        @Length(2, 20) val strWithLength: String,
        @RegularExpression(
            "^[0-9a-fA-F]*$",
            "The field strHexaDec should only contain hexadecimal digits"
        ) val strHexaDec: String
    )

    @Request("A Request with validated number fields")
    data class NumberValidatorsExample(
        @Min(0, "The value of field intWithMin should be a positive integer") val intWithMin: Int,
        @Clamp(1, 90) val intBetween: Int,
        @FMax(100.0) val floatMax: Float,
        @FClamp(0.0, 1.0, "The value of field floatBetween should be a between 0 and 1") val floatBetween: Float
    )

    @Response("A String Response")
    @Request("A String Request")
    data class StringUsable(val str: String)

    @Path("{a}")
    data class LongParam(@PathParam("A simple Long Param") val a: Long)

    @Response("A Long Response")
    data class LongResponse(val str: Long)

    enum class Tags(override val description: String) : APITag {
        EXAMPLE("Wow this is a tag?!")
    }

    enum class Scopes(override val description: String) : Described {
        profile("Basic Profile scope"), email("Email scope")
    }

    data class APIPrincipal(val a: String, val b: String)


    @Request("A LocalDate Request")
    data class LocalDateQuery(@QueryParam("LocalDate") val date: LocalDate)
    data class LocalDateOptionalQuery(@QueryParam("LocalDate") val date: LocalDate?)
    data class LocalDateTimeQuery(@QueryParam("LocalDateTime") val date: LocalDateTime)
    data class OffsetDateTimeQuery(@QueryParam("OffsetDateTime") val date: OffsetDateTime)
    data class ZonedDateTimeQuery(@QueryParam("OffsetDateTime") val date: ZonedDateTime)
    data class InstantQuery(@QueryParam("Instant") val date: Instant)

    data class LocalTimeQuery(@QueryParam("LocalTime") val time: LocalTime)
    data class OffsetTimeQuery(@QueryParam("OffsetTime") val time: OffsetTime)

    data class LocalDateResponse(val date: LocalDate?)
    data class LocalDateTimeResponse(val date: LocalDateTime?)
    data class OffsetDateTimeResponse(val date: OffsetDateTime?)
    data class ZonedDateTimeResponse(val date: ZonedDateTime?)
    data class InstantResponse(val instant: Instant)
    data class LocalTimeResponse(val time: LocalTime?)
    data class OffsetTimeResponse(val time: OffsetTime?)
}
