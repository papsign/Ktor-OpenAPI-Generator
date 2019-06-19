package com.papsign.ktor.openapigen.annotations.encodings

/**
 * must be applied to parser or serializer object, or annotation to mark it as Encoding Selector
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class APIFormatter

