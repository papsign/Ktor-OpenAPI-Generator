package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.PathParamStyle

data class PathParameterTranslation(val key: String, val style: PathParamStyle, val explode: Boolean) {
    val translatedName = "${style.prefix}$key${if (explode) "*" else ""}"
}
