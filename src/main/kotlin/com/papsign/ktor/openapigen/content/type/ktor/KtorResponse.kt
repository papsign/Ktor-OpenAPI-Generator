package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.ktor.openapigen.annotations.encodings.APIResponseFormat

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIResponseFormat
annotation class KtorResponse