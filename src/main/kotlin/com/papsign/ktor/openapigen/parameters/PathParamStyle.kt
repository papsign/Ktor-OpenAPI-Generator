package com.papsign.ktor.openapigen.parameters

import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.path.label.MatrixBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.path.matrix.LabelBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.path.simple.SimpleBuilderFactory


enum class PathParamStyle(override val factory: BuilderFactory<Builder<PathParamStyle>, PathParamStyle>): ParameterStyle<PathParamStyle> {
    simple(SimpleBuilderFactory), label(LabelBuilderFactory), matrix(MatrixBuilderFactory)
}
