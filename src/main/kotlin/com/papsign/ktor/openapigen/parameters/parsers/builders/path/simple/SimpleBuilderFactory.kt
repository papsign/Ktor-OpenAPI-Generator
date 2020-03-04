package com.papsign.ktor.openapigen.parameters.parsers.builders.path.simple

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory

object SimpleBuilderFactory: BuilderSelectorFactory<Builder<PathParamStyle>, PathParamStyle>(
    SimpleBuilder
)
