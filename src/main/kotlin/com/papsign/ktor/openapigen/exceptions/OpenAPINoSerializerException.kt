package com.papsign.ktor.openapigen.exceptions

import io.ktor.http.ContentType
import io.ktor.http.HeaderValue
import java.lang.Exception

class OpenAPINoSerializerException(val contentTypes: List<ContentType>): Exception("No serializer found for content types $contentTypes")