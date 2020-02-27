package com.papsign.ktor.openapigen.parameters.parsers

class ModularTranslator(pathModules: List<PathParameterTranslation>, queryModules: List<QueryParameterTranslation>) :
    OpenAPIPathSegmentTranslator {
    private val pathModules = pathModules.associate { it.key to it.translatedName }
    private val querySegment =
        queryModules.mapNotNull { it.translatedName }.let { if (it.isNotEmpty()) "{?${it.joinToString(",")}}" else "" }

    override fun getQuerySegment(): String = querySegment
    override fun translateSegment(segment: String): String {
        val match = pathRegex.matchEntire(segment) ?: return segment
        val key = match.groupValues.getOrNull(1) ?: return segment
        return pathModules[key]?.let { "{$it}" } ?: return segment
    }

    companion object {
        private val pathRegex = Regex("^\\{(.*)}$")
    }
}
