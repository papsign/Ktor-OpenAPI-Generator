package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.ktor.openapigen.content.type.ContentTypeProvider
import com.papsign.ktor.openapigen.route.OpenAPIRoute
import io.ktor.http.ContentType
import io.ktor.util.pipeline.ContextDsl


fun <T: OpenAPIRoute<T>> T.binaryParser(content: Set<ContentType>): T {
    return child().apply {
        content.forEach {
            provider.registerModule(BinaryContentTypeParser(it))
        }
    }
}

@ContextDsl
inline fun <T: OpenAPIRoute<T>> T.binaryParser(vararg content: ContentType, crossinline fn: T.() -> Unit) {
    binaryParser(content.toSet()).fn()
}
