package com.papsign.ktor.openapigen.parameters.parsers

object NoTranslation : OpenAPIPathSegmentTranslator {
    override fun translateSegment(segment: String): String = segment
    override fun getQuerySegment(): String = ""
}
