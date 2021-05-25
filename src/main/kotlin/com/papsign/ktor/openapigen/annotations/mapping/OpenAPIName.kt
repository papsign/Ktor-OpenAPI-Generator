package com.papsign.ktor.openapigen.annotations.mapping

import io.ktor.util.*
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class OpenAPIName(val name: String)

private val cache = Collections.synchronizedMap(HashMap<KParameter, String?>())

val KParameter.openAPIName: String?
    get() = cache.getOrPut(this) { findAnnotation<OpenAPIName>()?.name ?: name }

fun KParameter.remapOpenAPINames(headersOrParameters: StringValues): StringValues {
    val replacement = this.openAPIName
    val actual = this.name
    val map = headersOrParameters.toMap()

    val newMap = if (actual != null && replacement != actual) {
        map.mapKeys { (key, _) -> if (key == replacement) actual else key }
    } else map

    return valuesOf(newMap, headersOrParameters.caseInsensitiveName)
}