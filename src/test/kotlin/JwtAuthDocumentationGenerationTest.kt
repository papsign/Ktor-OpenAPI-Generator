package origo.booking

import TestServerWithJwtAuth.testServerWithJwtAuth
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test


internal class JwtAuthDocumentationGenerationTest {

    @Test
    fun testRequest() = withTestApplication({
        testServerWithJwtAuth()
    }) {
        with(handleRequest(HttpMethod.Get, "//openapi.json")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(
                response.content!!.contains(
                    "\"securitySchemes\" : {\n" +
                            "      \"jwtAuth\" : {\n" +
                            "        \"bearerFormat\" : \"JWT\",\n" +
                            "        \"scheme\" : \"bearer\",\n" +
                            "        \"type\" : \"http\"\n" +
                            "      }\n" +
                            "    }"
                )
            )
            assertTrue(
                response.content!!.contains(
                    "\"security\" : [ {\n" +
                            "          \"jwtAuth\" : [ ]\n" +
                            "        } ]"
                )
            )
        }
    }
}