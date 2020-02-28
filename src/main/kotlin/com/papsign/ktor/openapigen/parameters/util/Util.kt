package com.papsign.ktor.openapigen.parameters.util

import com.papsign.kotlin.reflection.getKType
import com.papsign.kotlin.reflection.toKType
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.handlers.ModularParameterHander
import com.papsign.ktor.openapigen.parameters.handlers.ParameterHandler
import com.papsign.ktor.openapigen.parameters.parsers.*
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure


private val log = classLogger<ParameterHandler<*>>()

fun <T> genPathParseFunc(key: String, style: PathParamStyle, parse: (String?) -> T): (String?) -> T {
    return when (style) {
        PathParamStyle.simple -> parse
        PathParamStyle.label -> ({ parse(it?.removePrefix(".")) })
        PathParamStyle.matrix -> ({ parse(it?.removePrefix(";$key=")) })
        else -> error("Path param style $style is not supported")
    }
}

inline fun <reified T> primitive(noinline cvt: (String?) -> T): Pair<KType, (String?) -> T> {
    return getKType<T>() to cvt
}

private val dateFormat = SimpleDateFormat()

val primitiveParsers = mapOf(
    primitive { it?.toIntOrNull() ?: 0 },
    primitive { it?.toIntOrNull() },
    primitive { it?.toLongOrNull() ?: 0 },
    primitive { it?.toLongOrNull() },
    primitive {
        it?.toBigIntegerOrNull() ?: BigInteger.ZERO
    },
    primitive { it?.toBigIntegerOrNull() },
    primitive {
        it?.toBigDecimalOrNull() ?: BigDecimal.ZERO
    },
    primitive { it?.toBigDecimalOrNull() },
    primitive { it?.toFloatOrNull() ?: 0f },
    primitive { it?.toFloatOrNull() },
    primitive { it?.toDoubleOrNull() ?: 0.0 },
    primitive { it?.toDoubleOrNull() },
    primitive { it?.toBoolean() ?: false },
    primitive { it?.toBoolean() },
    // removed temporarily because behavior may not be standard or expected
//    primitive { it?.toLongOrNull()?.let(::Date) ?: it?.let(dateFormat::parse) ?: Date() },
//    primitive { it?.toLongOrNull()?.let(::Date) ?: it?.let(dateFormat::parse) },
//    primitive { it?.toLongOrNull()?.let(Instant::ofEpochMilli) ?: it?.let(Instant::parse) ?: Instant.now() },
//    primitive { it?.toLongOrNull()?.let(Instant::ofEpochMilli) ?: it?.let(Instant::parse) },
    primitive {
        it?.let {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        } ?: ByteArray(16)
    },
    primitive {
        it?.let {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    },
    primitive { it ?: "" },
    primitive { it }
)

inline fun <reified T : Any> buildParameterHandler(): ParameterHandler<T> {
    if (Unit is T) return UnitParameterHandler as ParameterHandler<T>
    val t = T::class
    assert(t.isData) { "API route with ${t.simpleName} must be a data class." }
    val constructor = t.primaryConstructor ?: error("API routes with ${t.simpleName} must have a primary constructor.")
    val parsers = constructor.parameters.associateWith { param ->
        val key = param.name!! // cannot be null in Data Class
        val type = param.type
        val info = ParameterInfo(key, param)
        primitiveParsers[type]?.let { PrimitiveParameterParser(info, it) } ?: makeParameterParser(type, info)
    }
    return ModularParameterHander(
        parsers,
        constructor
    )
}


fun makeParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val clazz = type.jvmErasure
    val jclazz = clazz.java
    return when {
        jclazz.isEnum -> makeEnumParameterParser(
            type,
            info
        )
        clazz.isSubclassOf(List::class) -> {
            makeListParameterParser(type, info)
        }
        jclazz.isArray -> makeArrayParameterParser(
            type,
            info
        )
        clazz.isSubclassOf(Map::class) -> makeMapParameterParser(
            type,
            info
        )
        else -> makeObjectParameterParser(type, info)
    }
}

private fun makeEnumParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    return EnumParameterParser(
        info,
        type.jvmErasure.java.enumConstants.associateBy { (it as Enum<*>).name },
        type.isMarkedNullable
    )
}

private fun makeListParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val contentType = type.arguments[0].type!!
    return CollectionParameterParser<Any?, Any?>(
        info,
        contentType
    ) { it }
}

inline fun <reified T> primCVT(noinline cvt: (List<T>) -> Any): Pair<KType, (List<Any?>?) -> Any?> {
    return getKType<T>() to ({ lst ->
        lst?.let { cvt(it as List<T>) }
    })
}

val primCVT = mapOf(
    primCVT<Long> { it.toLongArray() },
    primCVT<Int> { it.toIntArray() },
    primCVT<Float> { it.toFloatArray() },
    primCVT<Double> { it.toDoubleArray() },
    primCVT<Boolean> { it.toBooleanArray() }
)

/**
 * you may think it is redundant but it is not. Maybe the nullable types are useless though.
 */
val arrCVT = mapOf(
    primCVT<Long> { it.toTypedArray() },
    primCVT<Long?> { it.toTypedArray() },
    primCVT<Int> { it.toTypedArray() },
    primCVT<Int?> { it.toTypedArray() },
    primCVT<Float> { it.toTypedArray() },
    primCVT<Float?> { it.toTypedArray() },
    primCVT<Double> { it.toTypedArray() },
    primCVT<Double?> { it.toTypedArray() },
    primCVT<Boolean> { it.toTypedArray() },
    primCVT<Boolean?> { it.toTypedArray() }
)


private fun makeArrayParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val contentType = type.jvmErasure.java.componentType.toKType()

    val cvt = if (type.toString().startsWith("kotlin.Array")) {
        arrCVT[contentType] ?: ({ lst -> lst?.toTypedArray() })
    } else {
        primCVT[contentType] ?: error("Arrays with primitive type $contentType are not supported")
    }
    return CollectionParameterParser<Any?, Any?>(
        info,
        contentType
    ) { cvt(it) }
}

private fun makeObjectParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    return ObjectParameterParser(info, type)
}

private fun makeMapParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    TODO("Implement $type")
//    val type = type.arguments[1].type ?: getKType<String>()
//    return Schema.SchemaMap(get(type).schema as Schema<Any>)
}
