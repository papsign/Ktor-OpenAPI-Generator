package com.papsign.ktor.openapigen.parameters

enum class PathParamStyle(val prefix: String): ParameterStyle {
    simple(""), label("."), matrix(";")
}
