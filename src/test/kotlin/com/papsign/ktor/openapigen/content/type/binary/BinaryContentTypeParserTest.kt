package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import installOpenAPI
import io.ktor.http.*
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Assert.*
import org.junit.Test
import java.io.InputStream
import kotlin.random.Random

const val contentType = "image/png"

class BinaryContentTypeParserTest {

    @BinaryRequest([contentType])
    @BinaryResponse([contentType])
    data class Stream(val stream: InputStream)

    @Test
    fun testBinaryParsing() {
        val route = "/test"
        val bytes = Random.nextBytes(20)
        withTestApplication({
            installOpenAPI()
            apiRouting {
                //(this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<Unit, Stream, Stream> { _, body ->
                        val actual = body.stream.readBytes()
                        assertArrayEquals(bytes, actual)
                        respond(Stream(actual.inputStream()))
                    }
                }
            }
        }) {

            println("Test: Normal")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, contentType)
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                assertEquals(ContentType.parse(contentType), response.contentType())
                assertArrayEquals(bytes, response.byteContent)
            }

            println("Test: Missing Accept")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, contentType)
                setBody(bytes)
            }.apply {
                assertEquals(ContentType.parse(contentType), response.contentType())
                assertArrayEquals(bytes, response.byteContent)
            }

            println("Test: Missing Content-Type")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.NotFound)
            }

            println("Test: Bad Accept")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, contentType)
                addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
                setBody(bytes)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.NotFound)
            }

            println("Test: Bad Content-Type")
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Accept, contentType)
                setBody(bytes)
            }.apply {
                assertEquals(response.status(), HttpStatusCode.NotFound)
            }
        }
    }
}