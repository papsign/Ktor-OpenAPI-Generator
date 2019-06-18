package com.papsign.ktor.openapigen.content.type.multipart

import io.ktor.http.ContentType
import java.io.InputStream

class NamedFileInputStream(val name: String?, contentType: ContentType?, inputStream: InputStream) : ContentInputStream(contentType, inputStream)