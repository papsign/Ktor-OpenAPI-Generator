package com.papsign.ktor.openapigen.parameters

interface ParameterStyle<S> where S: ParameterStyle<S>, S: Enum<S> {
    val name: String
}
