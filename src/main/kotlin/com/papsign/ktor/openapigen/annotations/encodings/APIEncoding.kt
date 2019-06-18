package com.papsign.ktor.openapigen.annotations.encodings

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
/**
 * must be applied to parser or serializer object, or annotation to mark it as Encoding Selector
 */
annotation class APIEncoding