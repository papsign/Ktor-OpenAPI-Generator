package com.papsign.ktor.openapigen

import TestServer.setupBaseTestServer
import com.papsign.ktor.openapigen.content.type.multipart.FormDataRequest
import com.papsign.ktor.openapigen.content.type.multipart.FormDataRequestType
import com.papsign.ktor.openapigen.content.type.multipart.NamedFileInputStream
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FormDocumentationGenerationTest {

    @Test
    fun formDataTestRequest() = withTestApplication({
        setupBaseTestServer()
        apiRouting {
            route("form-data"){
                post<Unit, TestServer.StringResponse, FormData>{ _, _ ->
                    respond(TestServer.StringResponse("result"))
                }
            }
        }
    }) {
        with(handleRequest(HttpMethod.Get, "//openapi.json")) {
            this@withTestApplication.application.log.debug(response.content)
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(
                response.content!!.contains(
                    """  "paths" : {
    "/form-data" : {
      "post" : {
        "requestBody" : {
          "content" : {
            "application/x-www-form-urlencoded" : {
              "schema" : {
                "${"$"}ref" : "#/components/schemas/FormData"
              }
            }
          }
        },"""
                )
            )

        }
    }

    @Test
    fun multipartFormDataTestRequest() = withTestApplication({
        setupBaseTestServer()
        apiRouting {
            route("multipart-data"){
                post<Unit, TestServer.StringResponse, MultiPartForm>{ _, _ ->
                    respond(TestServer.StringResponse("result"))
                }
            }
        }
    }) {
        with(handleRequest(HttpMethod.Get, "//openapi.json")) {
            this@withTestApplication.application.log.debug(response.content)
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(
                response.content!!.contains(
                    """  "paths" : {
    "/multipart-data" : {
      "post" : {
        "requestBody" : {
          "content" : {
            "multipart/form-data" : {
              "schema" : {
                "${"$"}ref" : "#/components/schemas/MultiPartForm"
              }
            }
          }
        },"""
                )
            )

        }
    }

    @Test
    fun defaultFormDataTestRequest() = withTestApplication({
        setupBaseTestServer()
        apiRouting {
            route("default-form-data"){
                post<Unit, TestServer.StringResponse, DefaultFormData>{ _, _ ->
                    respond(TestServer.StringResponse("result"))
                }
            }
        }
    }) {
        with(handleRequest(HttpMethod.Get, "//openapi.json")) {
            this@withTestApplication.application.log.debug(response.content)
            assertEquals(HttpStatusCode.OK, response.status())
            assertTrue(
                response.content!!.contains(
                    """  "paths" : {
    "/default-form-data" : {
      "post" : {
        "requestBody" : {
          "content" : {
            "multipart/form-data" : {
              "schema" : {
                "${"$"}ref" : "#/components/schemas/DefaultFormData"
              }
            }
          }
        },"""
                )
            )

        }
    }
}

@FormDataRequest(type = FormDataRequestType.MULTIPART)
data class MultiPartForm(val userId: String, val file: NamedFileInputStream)

@FormDataRequest(type = FormDataRequestType.URL_ENCODED)
data class FormData(val login: String, val password: String)

@FormDataRequest
data class DefaultFormData(val login: String, val password: String)
