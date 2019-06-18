package com.papsign.ktor.openapigen.content.type.multipart

import com.papsign.ktor.openapigen.annotations.encodings.APIEncoding

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIEncoding
annotation class FormDataRequest