package com.papsign.ktor.openapigen.parameters.handlers

import com.papsign.ktor.openapigen.parameters.parsers.ParameterParser
import io.ktor.http.Parameters
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter

class ModularParameterHander<T>(val parsers: Map<KParameter, ParameterParser>, val constructor: KFunction<T>) :
    ParameterHandler<T> {

    override fun parse(parameters: Parameters): T {
        return constructor.callBy(parsers.mapValues { it.value.parse(parameters) })
    }
}
