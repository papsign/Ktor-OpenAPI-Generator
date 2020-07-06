package com.papsign.ktor.openapigen.routing

import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import installJackson
import installOpenAPI
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.Routing
import io.ktor.server.testing.contentType
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertTrue

class GenericsTest {

    data class TestHeaderParams(@HeaderParam("test param") val `Test-Header`: MutableList<Long>)

    @Test
    fun testTypedMap() {
        val route = "/test"
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<TestHeaderParams, List<String>, Map<String, String>> { params, body ->
                        respond(mutableListOf(params.toString(), body.toString()))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", "1,2,3")
                setBody("{\"xyz\":456}")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    "[\"TestHeaderParams(Test-Header=[1, 2, 3])\",\"{xyz=456}\"]",
                    response.content
                )
            }
        }
    }

    @Test
    fun testTypedList() {
        val route = "/test"
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<TestHeaderParams, List<String>, List<String>> { params, body ->
                        respond(mutableListOf(params.toString(), body.toString()))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", "1,2,3")
                setBody("[\"a\",\"b\",\"c\"]")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    "[\"TestHeaderParams(Test-Header=[1, 2, 3])\",\"[a, b, c]\"]",
                    response.content
                )
            }
        }
    }

}