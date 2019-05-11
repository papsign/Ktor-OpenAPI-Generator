package com.papsign.ktor.openapigen.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Response(val description: String = "", val statusCode: Int = 200)