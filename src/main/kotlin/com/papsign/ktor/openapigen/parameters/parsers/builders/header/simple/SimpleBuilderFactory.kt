package com.papsign.ktor.openapigen.parameters.parsers.builders.header.simple

import com.papsign.ktor.openapigen.parameters.HeaderParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory

object SimpleBuilderFactory: BuilderSelectorFactory<Builder<HeaderParamStyle>, HeaderParamStyle>(
    SimpleBuilder
)
