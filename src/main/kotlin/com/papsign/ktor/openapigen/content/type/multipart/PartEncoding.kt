package com.papsign.ktor.openapigen.content.type.multipart

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PartEncoding(val contentType: String)