package com.papsign.ktor.openapigen.annotations.mapping

import com.papsign.ktor.openapigen.annotations.parameters.HeaderParam
import java.util.*
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class OpenAPIName(val name: String)

private val cache = Collections.synchronizedMap(HashMap<KParameter, String?>())

val KParameter.openAPIName: String?
    get() = cache.getOrPut(this) {
       val caseSensitiveName = findAnnotation<OpenAPIName>()?.name ?: name
       if (findAnnotation<HeaderParam>() != null) caseSensitiveName?.toLowerCase() else caseSensitiveName
    }

fun <T> KParameter.remapOpenAPINames(map: Map<String, T>): Map<String, T> {
    val replace = this.openAPIName
    val actual = this.name
    return if (actual != null && replace != actual) map.mapKeys { (key, _) -> if (key == replace) actual else key } else map
}
