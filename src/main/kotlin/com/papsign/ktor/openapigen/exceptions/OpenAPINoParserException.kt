package com.papsign.ktor.openapigen.exceptions

import io.ktor.http.ContentType
import java.lang.Exception

class OpenAPINoParserException(val contentType: ContentType): Exception("No parser found for content type $contentType")