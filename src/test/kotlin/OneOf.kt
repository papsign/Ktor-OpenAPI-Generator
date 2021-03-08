import TestServer.Setup
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.papsign.ktor.openapigen.annotations.type.`object`.example.ExampleProvider
import com.papsign.ktor.openapigen.annotations.type.`object`.example.WithExample
import com.papsign.ktor.openapigen.annotations.type.number.integer.clamp.Clamp
import com.papsign.ktor.openapigen.annotations.type.number.integer.max.Max
import com.papsign.ktor.openapigen.annotations.type.number.integer.min.Min
import com.papsign.ktor.openapigen.annotations.type.string.example.DiscriminatorAnnotation
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.info
import com.papsign.ktor.openapigen.route.path.normal.NormalOpenAPIRoute
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert
import org.junit.Test

fun NormalOpenAPIRoute.SealedRoute() {
    route("sealed") {
        post<Unit, Base, Base>(
            info("Sealed class Endpoint", "This is a Sealed class Endpoint"),
            exampleRequest = Base.A("Hi"),
            exampleResponse = Base.A("Hi")
        ) { params, base ->
            respond(base)
        }
    }
}


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@DiscriminatorAnnotation()
sealed class Base {
    @JsonTypeName("A")
    @DiscriminatorAnnotation()
    class A(val str: String) : Base()

    @JsonTypeName("B")
    @DiscriminatorAnnotation()
    class B(@Min(0) @Max(2) val i: Int) : Base()

    @WithExample
    @JsonTypeName("C")
    @DiscriminatorAnnotation()
    class C(@Clamp(0, 10) val l: Long) : Base() {
        companion object : ExampleProvider<C> {
            override val example: C = C(5)
        }
    }
}

val ref = "\$ref"

internal class OneOfLegacyGenerationTests {
    @Test
    fun willDiscriminatorsBePresent() = withTestApplication({
        Setup()
        apiRouting {
            SealedRoute()
        }
    }) {
        with(handleRequest(HttpMethod.Get, "//openapi.json")) {
            Assert.assertEquals(HttpStatusCode.OK, response.status())
            Assert.assertTrue(
                response.content!!.contains(
                    """"Base" : {
        "discriminator" : {
          "propertyName" : "type"
        },
        "oneOf" : [ {
          "$ref" : "#/components/schemas/A"
        }, {
          "$ref" : "#/components/schemas/B"
        }, {
          "$ref" : "#/components/schemas/C"
        } ],
        "properties" : {
          "type" : {
            "format" : "string",
            "nullable" : false,
            "type" : "string"
          }
        }"""
                )
            )
            Assert.assertTrue(
                response.content!!.contains(
                    """"A" : {
        "discriminator" : {
          "propertyName" : "type"
        },
        "nullable" : false,
        "properties" : {
          "str" : {
            "nullable" : false,
            "type" : "string"
          },
          "type" : {
            "format" : "string",
            "nullable" : false,
            "type" : "string"
          }
        },
        "required" : [ "str" ],
        "type" : "object"
      }"""
                )
            )
        }
    }
}