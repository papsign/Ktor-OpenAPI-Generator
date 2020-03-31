package com.papsign.ktor.openapigen.parameters

import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.header.simple.SimpleBuilderFactory


enum class HeaderParamStyle(override val factory: BuilderFactory<Builder<HeaderParamStyle>, HeaderParamStyle>): ParameterStyle<HeaderParamStyle> {
    simple(SimpleBuilderFactory)
}
