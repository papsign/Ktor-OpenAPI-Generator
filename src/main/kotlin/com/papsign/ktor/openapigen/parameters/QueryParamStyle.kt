package com.papsign.ktor.openapigen.parameters

enum class QueryParamStyle: ParameterStyle<QueryParamStyle> {
    DEFAULT, form, spaceDelimited, pipeDelimited, deepObject
}
