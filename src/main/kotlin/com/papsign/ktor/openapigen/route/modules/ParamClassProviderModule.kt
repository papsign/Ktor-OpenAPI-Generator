package com.papsign.ktor.openapigen.route.modules

import com.papsign.ktor.openapigen.generator.ParamBuilder
import com.papsign.ktor.openapigen.modules.providers.ParameterProvider
import com.papsign.ktor.openapigen.openapi.Parameter
import kotlin.reflect.KClass

class ParamClassProviderModule<T: Any>(private val c: KClass<T>): ParameterProvider {
    override fun getParameters(builder: ParamBuilder): List<Parameter<*>> {
        return builder.getParams(c)
    }
}