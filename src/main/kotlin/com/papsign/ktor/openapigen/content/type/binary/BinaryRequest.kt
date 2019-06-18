package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.ktor.openapigen.annotations.encodings.APIEncoding

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIEncoding
annotation class BinaryRequest(val contentTypes: Array<String>)