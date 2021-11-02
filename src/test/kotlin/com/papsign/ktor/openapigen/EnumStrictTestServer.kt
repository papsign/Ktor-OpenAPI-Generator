package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.annotations.Path
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.annotations.type.enum.StrictEnumParsing
import com.papsign.ktor.openapigen.exceptions.OpenAPIBadContentException
import com.papsign.ktor.openapigen.exceptions.OpenAPIRequiredFieldException
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.response.respond
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.server.testing.*
import kotlin.test.*

@StrictEnumParsing
enum class StrictTestEnum {
    VALID,
    ALSO_VALID,
}

@Path("/")
data class NullableStrictEnumParams(@QueryParam("") val type: StrictTestEnum? = null)

@Path("/")
data class NonNullableStrictEnumParams(@QueryParam("") val type: StrictTestEnum)

class EnumStrictTestServer {

    companion object {
        // test server for nullable enums
        private fun Application.nullableEnum() {
            install(OpenAPIGen)
            install(StatusPages) {
                exception<OpenAPIBadContentException> { e ->
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            }
            apiRouting {
                get<NullableStrictEnumParams, String> { params ->
                    if (params.type != null)
                        assertTrue { StrictTestEnum.values().contains(params.type) }
                    respond(params.type?.toString() ?: "null")
                }
            }
        }

        // test server for non-nullable enums
        private fun Application.nonNullableEnum() {
            install(OpenAPIGen)
            install(StatusPages) {
                exception<OpenAPIRequiredFieldException> { e ->
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
                exception<OpenAPIBadContentException> { e ->
                    call.respond(HttpStatusCode.BadRequest, e.localizedMessage)
                }
            }
            apiRouting {
                get<NonNullableStrictEnumParams, String> { params ->
                    assertTrue { StrictTestEnum.values().contains(params.type) }
                    respond(params.type.toString())
                }
            }
        }
    }

    @Test
    fun `nullable enum could be omitted and it will be null`() {
        withTestApplication({ nullableEnum() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("null", response.content)
            }
        }
    }

    @Test
    fun `nullable enum should be parsed correctly`() {
        withTestApplication({ nullableEnum() }) {
            handleRequest(HttpMethod.Get, "/?type=VALID").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("VALID", response.content)
            }
            handleRequest(HttpMethod.Get, "/?type=ALSO_VALID").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("ALSO_VALID", response.content)
            }
        }
    }

    @Test
    fun `nullable enum parsing should be case-sensitive and should throw on passing wrong case`() {
        withTestApplication({ nullableEnum() }) {
            handleRequest(HttpMethod.Get, "/?type=valid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "Invalid value [valid] for enum parameter of type StrictTestEnum. Expected: [VALID,ALSO_VALID]",
                    response.content
                )
            }
            handleRequest(HttpMethod.Get, "/?type=also_valid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "Invalid value [also_valid] for enum parameter of type StrictTestEnum. Expected: [VALID,ALSO_VALID]",
                    response.content
                )
            }
        }
    }

    @Test
    fun `nullable enum parsing should not parse values outside of enum`() {
        withTestApplication({ nullableEnum() }) {
            handleRequest(HttpMethod.Get, "/?type=what").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "Invalid value [what] for enum parameter of type StrictTestEnum. Expected: [VALID,ALSO_VALID]",
                    response.content
                )
            }
        }
    }

    @Test
    fun `non-nullable enum cannot be omitted`() {
        withTestApplication({ nonNullableEnum() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals("The field type is required", response.content)
            }
        }
    }

    @Test
    fun `non-nullable enum should be parsed correctly`() {
        withTestApplication({ nonNullableEnum() }) {
            handleRequest(HttpMethod.Get, "/?type=VALID").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("VALID", response.content)
            }
            handleRequest(HttpMethod.Get, "/?type=ALSO_VALID").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("ALSO_VALID", response.content)
            }
        }
    }

    @Test
    fun `non-nullable enum parsing should be case-sensitive and should throw on passing wrong case`() {
        withTestApplication({ nonNullableEnum() }) {
            handleRequest(HttpMethod.Get, "/?type=valid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "Invalid value [valid] for enum parameter of type StrictTestEnum. Expected: [VALID,ALSO_VALID]",
                    response.content
                )
            }
            handleRequest(HttpMethod.Get, "/?type=also_valid").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "Invalid value [also_valid] for enum parameter of type StrictTestEnum. Expected: [VALID,ALSO_VALID]",
                    response.content
                )
            }
        }
    }

    @Test
    fun `non-nullable enum parsing should not parse values outside of enum`() {
        withTestApplication({ nonNullableEnum() }) {
            handleRequest(HttpMethod.Get, "/?type=what").apply {
                assertEquals(HttpStatusCode.BadRequest, response.status())
                assertEquals(
                    "Invalid value [what] for enum parameter of type StrictTestEnum. Expected: [VALID,ALSO_VALID]",
                    response.content
                )
            }
        }
    }
}
