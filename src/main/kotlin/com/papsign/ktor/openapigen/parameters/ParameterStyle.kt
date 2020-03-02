package com.papsign.ktor.openapigen.parameters

import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderFactory

interface ParameterStyle<S> where S: ParameterStyle<S>, S: Enum<S> {
    val name: String
}
