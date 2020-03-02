package com.papsign.ktor.openapigen.parameters.parsers.generic

import com.papsign.ktor.openapigen.parameters.ParameterStyle

interface Builder<S> where S: ParameterStyle<S>, S: Enum<S> {
    val style: S
    val exploded: Boolean
    fun build(key: String, parameters: Map<String, List<String>>): Any?
}
