package com.papsign.ktor.openapigen.parameters.handlers

import com.papsign.ktor.openapigen.modules.providers.ParameterProvider
import io.ktor.http.Headers
import io.ktor.http.Parameters

interface ParameterHandler<T>: ParameterProvider {
    fun parse(parameters: Parameters, headers: Headers): T
}
