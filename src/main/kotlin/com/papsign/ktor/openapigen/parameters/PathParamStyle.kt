package com.papsign.ktor.openapigen.parameters


enum class PathParamStyle: ParameterStyle<PathParamStyle> {
    DEFAULT, simple, label, matrix
}
