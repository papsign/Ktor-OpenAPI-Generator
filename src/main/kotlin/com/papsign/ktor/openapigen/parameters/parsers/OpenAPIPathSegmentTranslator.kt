package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.modules.OpenAPIModule

interface OpenAPIPathSegmentTranslator : OpenAPIModule {
    fun translateSegment(segment: String): String
    fun getQuerySegment(): String
}
