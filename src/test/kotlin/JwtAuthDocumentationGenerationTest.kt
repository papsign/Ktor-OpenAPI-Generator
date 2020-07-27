package origo.booking

import TestServerWithJwtAuth.testServerWithJwtAuth
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.junit.Assert.*


internal class JwtAuthDocumentationGenerationTest {

    @Test
    fun testRequest() = withTestApplication({
        testServerWithJwtAuth()
    }) {
        with(handleRequest(HttpMethod.Get, "//openapi.json")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(response.content!!.contains("\"securitySchemes\" : {\n" +
                    "      \"JWT\" : {\n" +
                    "        \"bearerFormat\" : \"JWT\",\n" +
                    "        \"name\" : \"JWT\",\n" +
                    "        \"scheme\" : \"bearer\",\n" +
                    "        \"type\" : \"openIdConnect\"\n" +
                    "      }\n" +
                    "    }"))
            assertTrue(response.content!!.contains("\"security\" : [ { } ],"))
        }
    }

}