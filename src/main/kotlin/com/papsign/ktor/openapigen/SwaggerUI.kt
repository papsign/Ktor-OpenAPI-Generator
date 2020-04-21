package com.papsign.ktor.openapigen

import io.ktor.application.ApplicationCall
import io.ktor.features.origin
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Image.PNG
import io.ktor.http.ContentType.Text.CSS
import io.ktor.http.ContentType.Text.Html
import io.ktor.http.ContentType.Text.JavaScript
import io.ktor.http.content.OutgoingContent
import io.ktor.http.withCharset
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import java.net.URL

class SwaggerUi(private val basePath: String, private val version: String) {
    private val notFound = mutableListOf<String>()
    private val content = mutableMapOf<String, ResourceContent>()
    suspend fun serve(filename: String?, call: ApplicationCall) {
        when (filename) {
            in notFound -> return
            null -> return
            else -> {
                val resource = this::class.java.getResource("/META-INF/resources/webjars/swagger-ui/$version/$filename")
                if (resource == null) {
                    notFound.add(filename)
                    return
                }
                call.respond(content.getOrPut(filename) { ResourceContent(resource, call.redirectUrl()) })
            }
        }
    }

    private fun ApplicationCall.redirectUrl(): String {
        val defaultPort = if (request.origin.scheme == "http") 80 else 443
        val hostPort = request.host() + request.port().let { port -> if (port == defaultPort) "" else ":$port" }
        val protocol = request.origin.scheme
        return "$protocol://$hostPort/${basePath.trim('/')}/"
    }
}



private val contentTypes = mapOf(
    "html" to Html,
    "css" to CSS,
    "js" to JavaScript,
    "json" to ContentType.Application.Json.withCharset(Charsets.UTF_8),
    "png" to PNG)

private class ResourceContent(val resource: URL, val address: String) : OutgoingContent.ByteArrayContent() {
    private val bytes by lazy {
        if (contentType == JavaScript) {
            resource.readText().replace("http://localhost:3200/oauth2-redirect.html", address + "oauth2-redirect.html").toByteArray()
        } else resource.readBytes()
    }

    override val contentType: ContentType? by lazy {
        val extension = resource.file.substring(resource.file.lastIndexOf('.') + 1)
        contentTypes[extension] ?: Html
    }

    override val contentLength: Long? by lazy {
        bytes.size.toLong()
    }

    override fun bytes(): ByteArray = bytes
    override fun toString() = "ResourceContent \"$resource\""
}