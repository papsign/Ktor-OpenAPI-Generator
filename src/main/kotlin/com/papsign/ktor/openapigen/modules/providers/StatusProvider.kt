package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.modules.OpenAPIModule
import io.ktor.http.HttpStatusCode
import kotlin.reflect.KType

interface StatusProvider : OpenAPIModule {
    fun getStatusForType(responseType: KType): HttpStatusCode
}
