# Ktor_OpenAPI_Generator
The Ktor OpenAPI Generator is a library to automatically generate the descriptor as you route your ktor application.

## The project is in ALPHA, some features may be broken.

Ktor OpenAPI Generator is:
- Modular
- Strongly typed
- Explicit

Currently Supported:
- Authentication ineroperability with strongly typed Principal (OAuth only, see TestServer in tests)
- Content Negociation interoperability (see TestServer in tests)
- Custom response codes (as parameter in @Response)
- Automatic and custom content Type routing and parsing (see com.papsign.ktor.openapigen.content.type, Binary Parser and default JSON parser (that uses the ktor implicit parsing/serializing))
- Exception handling (use .throws(ex) {} in the routes with an APIException object) with Status pages interop (with .withAPI in the StatusPages configuration)
- tags (.tag(tag) {} in route with a tag object, currently must be an enum, but may be subject to change)
- Parameter Parsing (see basic example), only supports primitive types currently, needs to be put up to openapi specification.

It is inspired by ktor Locations, but makes no use of it.

Minimal Example:

```kotlin

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
                schemaNamer = {
                    //rename DTOs from java type name to generator compatible form
                    val regex = Regex("[A-Za-z0-9_.]+")
                    it.toString().replace(regex) { it.value.split(".").last() }.replace(Regex(">|<|, "), "_")
                }
            }

            install(ContentNegotiation) {
                jackson()
            }

            // normal Ktor routing
            routing {
                get("/openapi.json") {
                    call.respond(application.openAPIGen.api)
                }

                get("/") {
                    call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
                }
            }

            //Described routing
            apiRouting {

                //bare minimum, just like Ktor but strongly typed
                get<StringParam, StringResponse> { params ->
                    respond(StringResponse(params.a))
                }
                
                route("inine").get<StringParam, StringResponse>(
                    info("String Param Endpoint", "This is a String Param Endpoint"), // A Route module that describes an endpoint, it is optional
                    example = StringResponse("Hi")
                ) { params ->
                    respond(StringResponse(params.a))
                }

                route("block") {
                    // use Unit if there are no parameters / body / response
                    post<Unit, StringUsable,  StringUsable>(
                        info("String Post Endpoint", "This is a String Post Endpoint"),
                        exampleRequest = StringUsable("Ho"),
                        exampleResponse = StringUsable("Ho")
                    ) { params, body ->
                        respond(body)
                    }
                }
            }
        }.start(true)

    }

    // Path works like the @Location from locations, but for transparency we recommend only using it to extract the parameters
    @Path("string/{a}")
    data class StringParam(
        @PathParam("A simple String Param") val a: String,
        @QueryParam("Optional String") val optional: String? // Nullable Types are optional
    )

    // A response can be any class, but a description will be generated from the annotation
    @Response("A String Response")
    data class StringResponse(val str: String)


    // DTOs can be requests and responses, annotations are optional
    @Response("A String Response")
    @Request("A String Request")
    data class StringUsable(val str: String)
}
```

For an advanced example with most of the features, see the tests.


