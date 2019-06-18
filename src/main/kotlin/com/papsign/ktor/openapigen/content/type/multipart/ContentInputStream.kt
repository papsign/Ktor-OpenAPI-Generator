package com.papsign.ktor.openapigen.content.type.multipart

import io.ktor.http.ContentType
import java.io.BufferedInputStream
import java.io.InputStream

open class ContentInputStream(val contentType: ContentType?, inputStream: InputStream): BufferedInputStream(inputStream)