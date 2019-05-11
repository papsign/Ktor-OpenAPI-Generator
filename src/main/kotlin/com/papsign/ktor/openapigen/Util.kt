package com.papsign.ktor.openapigen

import io.ktor.application.Application
import io.ktor.application.feature
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Application.openAPIGen: OpenAPIGen get() = feature(OpenAPIGen)


internal fun Any.classLogger(): Logger {
    return LoggerFactory.getLogger(this::class.java)
}

internal inline fun <reified T> classLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}