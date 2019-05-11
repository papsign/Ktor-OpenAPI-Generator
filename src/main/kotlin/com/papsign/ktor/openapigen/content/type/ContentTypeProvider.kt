package com.papsign.ktor.openapigen.content.type

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import com.papsign.ktor.openapigen.openapi.MediaType
import io.ktor.http.ContentType
import kotlin.reflect.KType

interface ContentTypeProvider: OpenAPIModule {
    val contentType: ContentType
    fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T? = null): MediaType<T>?
}