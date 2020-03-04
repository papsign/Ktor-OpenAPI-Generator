package com.papsign.ktor.openapigen.parameters

import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderFactory

interface ParameterStyle<S> where S: ParameterStyle<S>, S: Enum<S> {
    val name: String
    val factory: BuilderFactory<Builder<S>, S>
}
