package com.papsign.ktor.openapigen.content.type.ktor

import com.papsign.ktor.openapigen.annotations.encodings.APIRequestFormat


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIRequestFormat
annotation class KtorRequest

