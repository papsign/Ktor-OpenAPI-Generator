package com.papsign.ktor.openapigen.parameters.parsers.builders.path.matrix

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory

object MatrixBuilderFactory: BuilderSelectorFactory<Builder<PathParamStyle>, PathParamStyle>(
    MatrixBuilder
)
