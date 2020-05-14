# Ktor OpenAPI Generator
[![](https://jitpack.io/v/papsign/Ktor-OpenAPI-Generator.svg)](https://jitpack.io/#papsign/Ktor-OpenAPI-Generator)
[![Build](https://github.com/papsign/Ktor-OpenAPI-Generator/workflows/Build/badge.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator/actions)

The Ktor OpenAPI Generator is a library to automatically generate the descriptor as you route your ktor application.

Ktor OpenAPI Generator is:
- Modular
- Strongly typed
- Explicit

Currently Supported:
- Authentication interoperability with strongly typed Principal (OAuth only, see TestServer in tests)
- Content Negotiation interoperability (see TestServer in tests)
- Custom response codes (as parameter in `@Response`)
- Automatic and custom content Type routing and parsing (see `com.papsign.ktor.openapigen.content.type`, Binary Parser and default JSON parser (that uses the ktor implicit parsing/serializing))
- Exception handling (use `.throws(ex) {}` in the routes with an APIException object) with Status pages interop (with .withAPI in the StatusPages configuration)
- tags (`.tag(tag) {}` in route with a tag object, currently must be an enum, but may be subject to change)
- Spec compliant Parameter Parsing (see basic example)

Extra Features:
- Includes Swagger-UI (enabled by default, can be managed in the `install(OpenAPIGen) { ... }` section)

It is inspired by ktor Locations, but makes no use of it.

Take a look at the [A few examples](https://github.com/papsign/Ktor-OpenAPI-Generator/wiki/A-few-examples)

## Installation

### Gradle

Step 1. Add the JitPack repository to your build file:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency:
```
dependencies {
        implementation 'com.github.papsign:Ktor-OpenAPI-Generator:-SNAPSHOT'
}
```

### Git Submodule
Install the submodule:
```
git submodule add https://github.com/papsign/Ktor-OpenAPI-Generator.git openapigen
```

Declare the folder in settings.gradle:
```
...
include 'openapigen'
```
Declare the dependency in the main build.gradle
```
apply plugin: 'kotlin'

...

dependencies {
    compile project(":openapigen")
    ...
}
```

## Examples

Basic Example:

```kotlin
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
```

Creates this `openapi.json` description:

```json
{
   "components":{
      "schemas":{
         "StringResponse":{
            "nullable":false,
            "properties":{
               "str":{
                  "nullable":false,
                  "type":"string"
               }
            },
            "required":[
               "str"
            ],
            "type":"object"
         },
         "StringUsable":{
            "nullable":false,
            "properties":{
               "str":{
                  "nullable":false,
                  "type":"string"
               }
            },
            "required":[
               "str"
            ],
            "type":"object"
         }
      }
   },
   "info":{
      "contact":{
         "email":"support@test.com",
         "name":"Support"
      },
      "description":"The Test API",
      "title":"Test API",
      "version":"0.0.1"
   },
   "openapi":"3.0.0",
   "paths":{
      "/string/{a}":{
         "get":{
            "parameters":[
               {
                  "deprecated":false,
                  "description":"A simple String Param",
                  "explode":false,
                  "in":"path",
                  "name":"a",
                  "required":true,
                  "schema":{
                     "nullable":false,
                     "type":"string"
                  },
                  "style":"simple"
               },
               {
                  "allowEmptyValue":false,
                  "deprecated":false,
                  "description":"Optional String",
                  "explode":false,
                  "in":"query",
                  "name":"optional",
                  "required":false,
                  "schema":{
                     "nullable":false,
                     "type":"string"
                  },
                  "style":"form"
               }
            ],
            "responses":{
               "200":{
                  "content":{
                     "application/json":{
                        "schema":{
                           "$ref":"#/components/schemas/StringResponse"
                        }
                     }
                  },
                  "description":"A String Response"
               }
            }
         }
      },
      "/inine/string/{a}":{
         "get":{
            "description":"This is a String Param Endpoint",
            "parameters":[
               {
                  "deprecated":false,
                  "description":"A simple String Param",
                  "explode":false,
                  "in":"path",
                  "name":"a",
                  "required":true,
                  "schema":{
                     "nullable":false,
                     "type":"string"
                  },
                  "style":"simple"
               },
               {
                  "allowEmptyValue":false,
                  "deprecated":false,
                  "description":"Optional String",
                  "explode":false,
                  "in":"query",
                  "name":"optional",
                  "required":false,
                  "schema":{
                     "nullable":false,
                     "type":"string"
                  },
                  "style":"form"
               }
            ],
            "responses":{
               "200":{
                  "content":{
                     "application/json":{
                        "example":{
                           "str":"Hi"
                        },
                        "schema":{
                           "$ref":"#/components/schemas/StringResponse"
                        }
                     }
                  },
                  "description":"A String Response"
               }
            },
            "summary":"String Param Endpoint"
         }
      },
      "/block":{
         "post":{
            "description":"This is a String Post Endpoint",
            "requestBody":{
               "content":{
                  "application/json":{
                     "example":{
                        "str":"Ho"
                     },
                     "schema":{
                        "$ref":"#/components/schemas/StringUsable"
                     }
                  }
               },
               "description":"A String Request"
            },
            "responses":{
               "200":{
                  "content":{
                     "application/json":{
                        "example":{
                           "str":"Ho"
                        },
                        "schema":{
                           "$ref":"#/components/schemas/StringUsable"
                        }
                     }
                  },
                  "description":"A String Response"
               }
            },
            "summary":"String Post Endpoint"
         }
      }
   },
   "servers":[
      {
         "description":"Test server",
         "url":"http://localhost:8080/"
      }
   ]
}
```

Full Example:

```kotlin
import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.Request
import com.papsign.ktor.openapigen.annotations.Response
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.openAPIGen
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

object Minimal {

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


