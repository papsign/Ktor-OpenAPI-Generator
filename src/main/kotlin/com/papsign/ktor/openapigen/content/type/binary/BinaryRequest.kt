package com.papsign.ktor.openapigen.content.type.binary

import com.papsign.ktor.openapigen.annotations.encodings.APIRequestFormat

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIRequestFormat
annotation class BinaryRequest(val contentTypes: Array<String>)

