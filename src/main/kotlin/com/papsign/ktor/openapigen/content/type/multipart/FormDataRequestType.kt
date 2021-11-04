package com.papsign.ktor.openapigen.content.type.multipart

import io.ktor.http.ContentType

enum class FormDataRequestType(val contentType: ContentType){
    MULTIPART(ContentType.MultiPart.FormData),
    URL_ENCODED(ContentType.Application.FormUrlEncoded)
}
