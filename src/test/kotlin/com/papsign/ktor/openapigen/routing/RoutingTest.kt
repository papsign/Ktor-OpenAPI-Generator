package com.papsign.ktor.openapigen.routing

import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.get
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

class RoutingTest {

    data class TestHeaderParams(@HeaderParam("test param") val `Test-Header`: Long)
    data class TestBodyParams(val xyz: Long)
    data class TestResponse(val msg: String)

    @Test
    fun testPostWithHeaderAndBodyParams() {
        val route = "/test"
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<TestHeaderParams, TestResponse, TestBodyParams> { params, body ->
                        respond(TestResponse("$params -> $body"))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.ContentType, "application/json")
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", "123")
                setBody("{\"xyz\":456}")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    "{\"msg\":\"${TestHeaderParams(123)} -> ${TestBodyParams(456)}\"}",
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetWithHeaderParams() {
        val route = "/test"
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<TestHeaderParams, TestResponse> { params ->
                        respond(TestResponse("$params"))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Get, route) {
                addHeader(HttpHeaders.Accept, "application/json")
                addHeader("Test-Header", "123")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    "{\"msg\":\"${TestHeaderParams(123)}\"}",
                    response.content
                )
            }
        }
    }

    @Test
    fun testPostWithUnitTypes() {
        val route = "/test"
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    post<Unit, TestResponse, Unit> { params, body ->
                        respond(TestResponse("Test Response"))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Post, route) {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    "{\"msg\":\"Test Response\"}",
                    response.content
                )
            }
        }
    }

    @Test
    fun testGetWithUnitTypes() {
        val route = "/test"
        withTestApplication({
            installOpenAPI()
            installJackson()
            apiRouting {
                (this.ktorRoute as Routing).trace { println(it.buildText()) }
                route(route) {
                    get<Unit, TestResponse> { params ->
                        respond(TestResponse("Test Response"))
                    }
                }
            }
        }) {
            handleRequest(HttpMethod.Get, route) {
                addHeader(HttpHeaders.Accept, "application/json")
            }.apply {
                assertTrue { response.contentType().match("application/json") }
                assertEquals(
                    "{\"msg\":\"Test Response\"}",
                    response.content
                )
            }
        }
    }
}