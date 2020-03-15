package com.papsign.ktor.openapigen

import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.typeOf

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
