package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.handlers.ParameterHandler
import io.ktor.http.Parameters

object UnitParameterHandler :
    ParameterHandler<Unit> {
    override fun parse(parameters: Parameters) = Unit
}
