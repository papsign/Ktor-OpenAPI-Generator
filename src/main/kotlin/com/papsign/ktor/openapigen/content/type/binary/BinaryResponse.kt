package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.ktor.openapigen.annotations.encodings.APIResponseFormat

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIResponseFormat
annotation class BinaryResponse(val contentTypes: Array<String>)