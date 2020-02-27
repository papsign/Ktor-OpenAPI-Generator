package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.kotlin.reflection.getKType
import com.papsign.kotlin.reflection.toKType
import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.annotations.parameters.QueryParam
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import io.ktor.http.Parameters
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure


interface ParameterHandler<T> {
    fun parse(parameters: Parameters): T
    val translator: OpenAPIPathSegmentTranslator
}

object UnitParameterHandler : ParameterHandler<Unit> {
    override val translator: OpenAPIPathSegmentTranslator = NoTranslation
    override fun parse(parameters: Parameters) = Unit
}

private val log = classLogger<ParameterHandler<*>>()

interface ParameterParser {
    val key: String
    val reservedKeys: Set<String>
        get() = setOf(key)

    fun parse(parameters: Parameters): Any?
}

interface PathParameterParser : ParameterParser {
    val translation: PathParameterTranslation
    val style: PathParamStyle
    val explode: Boolean
}

interface QueryParameterParser : ParameterParser {
    val translation: QueryParameterTranslation
    val style: QueryParamStyle
    val explode: Boolean
}

private fun <T> genPathParseFunc(key: String, style: PathParamStyle, parse: (String?) -> T): (String?) -> T {
    return when (style) {
        PathParamStyle.simple -> parse
        PathParamStyle.label -> ({ parse(it?.removePrefix(".")) })
        PathParamStyle.matrix -> ({ parse(it?.removePrefix(";$key=")) })
    }
}

class PrimitivePathParameterParser<T>(
    override val key: String,
    val parse: (String?) -> T,
    override val style: PathParamStyle,
    override val explode: Boolean
) : PathParameterParser {
    override val translation: PathParameterTranslation = PathParameterTranslation(key, style, explode)

    private val parseFunc = genPathParseFunc(key, style, parse)

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }
}

class PrimitiveQueryParameterParser<T>(
    override val key: String,
    val parse: (String?) -> T,
    style: QueryParamStyle,
    override val explode: Boolean
) : QueryParameterParser {
    init {
        if (style != QueryParamStyle.form)
            log.warn("Using non-form style for primitive type, it is undefined in the OpenAPI standard, reverting to form style")
    }

    override val style: QueryParamStyle = QueryParamStyle.form
    override val translation: QueryParameterTranslation = QueryParameterTranslation(key, this.style, explode)
    override fun parse(parameters: Parameters): Any? {
        return parse(parameters[key])
    }
}

class EnumQueryParameterParser(info: ParameterInfo, val enumMap: Map<String, *>, val nullable: Boolean) :
    QueryParameterParser {
    override val key: String = info.key

    init {
        if (info.queryAnnotation?.style != QueryParamStyle.form)
            log.warn("Using non-form style for enum type, it is undefined in the OpenAPI standard, reverting to form style")
    }

    override val style: QueryParamStyle = QueryParamStyle.form
    override val explode: Boolean = info.queryAnnotation!!.explode
    override val translation: QueryParameterTranslation = QueryParameterTranslation(key, style, explode)

    override fun parse(parameters: Parameters): Any? {
        return parameters[key]?.let { enumMap[it] }
    }
}

class EnumPathParameterParser(info: ParameterInfo, val enumMap: Map<String, *>, val nullable: Boolean) :
    PathParameterParser {
    override val key: String = info.key
    override val style: PathParamStyle = info.pathAnnotation!!.style
    override val explode: Boolean = info.pathAnnotation!!.explode
    override val translation: PathParameterTranslation = PathParameterTranslation(key, style, explode)

    private fun parse(parameter: String?): Any? {
        return parameter?.let { enumMap[it] }
    }

    private val parseFunc = genPathParseFunc(key, style, ::parse)

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }
}

class CollectionPathParameterParser<T, A>(info: ParameterInfo, type: KType, val cvt: (List<T>?) -> A) :
    PathParameterParser {
    override val key: String = info.key
    override val style: PathParamStyle = info.pathAnnotation!!.style
    override val explode: Boolean = info.pathAnnotation!!.explode
    override val translation: PathParameterTranslation = PathParameterTranslation(key, style, explode)

    private val typeParser =
        primitiveParsers[type] as ((String?) -> T)? ?: error("Non-primitive Arrays aren't yet supported")
    private val parseFunc: (String?) -> A = when (style) {
        PathParamStyle.simple -> ({ str: String? -> cvt(str?.split(',')?.map(typeParser)) })
        PathParamStyle.label -> {
            if (explode) {
                ({ str: String? -> cvt(str?.split('.')?.drop(1)?.map(typeParser)) })
            } else {
                ({ str: String? -> cvt(str?.removePrefix(".")?.split(',')?.map(typeParser)) })
            }
        }
        PathParamStyle.matrix -> {
            if (explode) {
                ({ str: String? -> cvt(str?.split(";$key=")?.drop(1)?.map(typeParser)) })
            } else {
                ({ str: String? -> cvt(str?.removePrefix(";$key=")?.split(',')?.map(typeParser)) })
            }
        }
    }

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }
}

class CollectionQueryParameterParser<T, A>(info: ParameterInfo, type: KType, val cvt: (List<T>?) -> A) :
    QueryParameterParser {
    override val key: String = info.key
    override val style: QueryParamStyle = info.queryAnnotation!!.style
    override val explode: Boolean = info.queryAnnotation!!.explode
    override val translation: QueryParameterTranslation = QueryParameterTranslation(key, style, explode)

    private val typeParser =
        primitiveParsers[type] as ((String?) -> T)? ?: error("Non-primitive Arrays aren't yet supported")
    private val explodedParse = ({ parameters: Parameters -> cvt(parameters.getAll(key)?.map(typeParser)) })
    private val parseFunc: (Parameters) -> A = when (style) {
        QueryParamStyle.form -> {
            if (explode) {
                explodedParse
            } else {
                ({ parameters: Parameters -> cvt(parameters[key]?.split(',')?.map(typeParser)) })
            }
        }
        QueryParamStyle.pipeDelimited -> {
            if (explode) {
                explodedParse
            } else {
                ({ parameters: Parameters -> cvt(parameters[key]?.split('|')?.map(typeParser)) })
            }
        }
        QueryParamStyle.spaceDelimited -> {
            if (explode) {
                explodedParse
            } else {
                ({ parameters: Parameters -> cvt(parameters[key]?.split(' ')?.map(typeParser)) })
            }
        }
        QueryParamStyle.deepObject -> error("Deep Objects are not supported for Arrays")
    }

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters)
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
    primitive { it?.toBigIntegerOrNull() ?: BigInteger.ZERO },
    primitive { it?.toBigIntegerOrNull() },
    primitive { it?.toBigDecimalOrNull() ?: BigDecimal.ZERO },
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

class ModularParameterHander<T>(val parsers: Map<KParameter, ParameterParser>, val constructor: KFunction<T>) :
    ParameterHandler<T> {

    override val translator: OpenAPIPathSegmentTranslator = ModularTranslator(
        parsers.values.filterIsInstance<PathParameterParser>().map { it.translation },
        parsers.values.filterIsInstance<QueryParameterParser>().map { it.translation }
    )

    override fun parse(parameters: Parameters): T {
        return constructor.callBy(parsers.mapValues { it.value.parse(parameters)?.also { println(it::class) } })
    }
}

inline fun <reified T : Any> buildParameterHandler(): ParameterHandler<T> {
    if (Unit is T) return UnitParameterHandler as ParameterHandler<T>
    val t = T::class
    assert(t.isData) { "API route with ${t.simpleName} must be a data class." }
    val constructor = t.primaryConstructor ?: error("API routes with ${t.simpleName} must have a primary constructor.")
    val parsers = constructor.parameters.associateWith { param ->
        val key = param.name!! // cannot be null in Data Class
        val type = param.type
        val info = ParameterInfo(key, param)
        primitiveParsers[type]?.let {
            if (info.pathAnnotation != null) {
                PrimitivePathParameterParser(key, it, info.pathAnnotation.style, info.pathAnnotation.explode)
            } else {
                PrimitiveQueryParameterParser(key, it, info.queryAnnotation!!.style, info.queryAnnotation.explode)
            }
        } ?: makeParameterParser(type, info)
    }
    return ModularParameterHander(parsers, constructor)
}


data class ParameterInfo(
    val key: String,
    val pathAnnotation: PathParam? = null,
    val queryAnnotation: QueryParam? = null
) {
    constructor(key: String, parameter: KParameter) : this(
        key,
        parameter.findAnnotation<PathParam>(),
        parameter.findAnnotation<QueryParam>()
    )
}


fun makeParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val clazz = type.jvmErasure
    val jclazz = clazz.java
    return when {
        jclazz.isEnum -> makeEnumParameterParser(type, info)
        clazz.isSubclassOf(List::class) -> {
            makeListParameterParser(type, info)
        }
        jclazz.isArray -> makeArrayParameterParser(type, info)
        clazz.isSubclassOf(Map::class) -> makeMapParameterParser(type, info)
        else -> makeObjectParameterParser(type, info)
    }
}

private fun makeEnumParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    return if (info.pathAnnotation != null) {
        EnumPathParameterParser(
            info,
            type.jvmErasure.java.enumConstants.associateBy { (it as Enum<*>).name },
            type.isMarkedNullable
        )
    } else {
        EnumQueryParameterParser(
            info,
            type.jvmErasure.java.enumConstants.associateBy { (it as Enum<*>).name },
            type.isMarkedNullable
        )
    }
}

private fun makeListParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val contentType = type.arguments[0].type!!
    return if (info.pathAnnotation != null) {
        CollectionPathParameterParser<Any?, Any?>(info, contentType) { it }
    } else {
        CollectionQueryParameterParser<Any?, Any?>(info, contentType) { it }
    }
}

inline fun <reified T> primCVT(noinline cvt: (List<T>) -> Any): Pair<KType, (List<Any?>?) -> Any?> {
    return getKType<T>() to ({ lst ->
        lst?.let {
            cvt(
                it as List<T>
            )
        }
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
    return if (info.pathAnnotation != null) {
        CollectionPathParameterParser<Any?, Any?>(info, contentType) { cvt(it) }
    } else {
        CollectionQueryParameterParser<Any?, Any?>(info, contentType) { cvt(it) }
    }
}

private fun makeObjectParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    TODO("Implement $type")
    val erasure = type.jvmErasure
//    if (erasure.isSealed) {
//        return Schema.OneSchemaOf(erasure.sealedSubclasses.map { get(it.starProjectedType).schema })
//    }
//    val props = erasure.declaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }
//    val properties = props.associate {
//        Pair(it.name, get(it.returnType).schema)
//    }
//    if (properties.isEmpty()) log.warn("No public properties found in object $type")
//    return Schema.SchemaObj<Any>(
//        properties,
//        props.filter { !it.returnType.isMarkedNullable }.map { it.name })
}

private fun makeMapParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    TODO("Implement $type")
//    val type = type.arguments[1].type ?: getKType<String>()
//    return Schema.SchemaMap(get(type).schema as Schema<Any>)
}
