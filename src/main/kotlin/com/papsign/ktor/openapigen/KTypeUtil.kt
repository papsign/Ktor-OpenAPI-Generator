package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.annotations.mapping.openAPIName
import java.lang.reflect.Field
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure

val unitKType = getKType<Unit>()

inline fun <reified T> isNullable(): Boolean {
    return null is T
}

inline fun <reified T> getKType() = typeOf<T>()

fun KType.strip(nullable: Boolean = isMarkedNullable): KType {
    return jvmErasure.createType(arguments, nullable)
}

fun KType.deepStrip(nullable: Boolean = isMarkedNullable): KType {
    return jvmErasure.createType(arguments.map { it.copy(type = it.type?.deepStrip()) }, nullable)
}

data class KTypeProperty(
    val name: String,
    val type: KType,
    val source: KProperty1<*, *>
)

val KType.memberProperties: List<KTypeProperty>
    get() {
        val typeParameters = jvmErasure.typeParameters.zip(arguments).associate { Pair(it.first.name, it.second.type) }
        return jvmErasure.memberProperties.map {
            val retType = it.returnType
            val properType = when (val classifier = retType.classifier) {
                is KTypeParameter -> typeParameters[classifier.name] ?: it.returnType
                else -> it.returnType
            }
            KTypeProperty(it.name, properType, it)
        }
    }

val KClass<*>.isInterface get() = java.isInterface
