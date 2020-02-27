package com.papsign.ktor.openapigen.parameters.handlers

import io.ktor.http.Parameters

interface ParameterHandler<T> {
    fun parse(parameters: Parameters): T
}
