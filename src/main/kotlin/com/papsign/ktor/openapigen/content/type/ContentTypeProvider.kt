package com.papsign.ktor.openapigen.content.type

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.MediaTypeModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule
import io.ktor.http.ContentType
import kotlin.reflect.KType

interface ContentTypeProvider: OpenAPIModule {

    enum class Usage {
        SERIALIZE, PARSE
    }

    /**
     * Done once when routes are created, for request object and response object
     * @return null to disable module, or [Map] to register the handler on every content type
     * @throws Exception to signal a bad configuration (usually with assert)
     */
    fun <T> getMediaType(type: KType, apiGen: OpenAPIGen, provider: ModuleProvider<*>, example: T?, usage: Usage): Map<ContentType, MediaTypeModel<T>>?
}
