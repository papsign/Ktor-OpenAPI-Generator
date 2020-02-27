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
import java.time.Instant
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure


interface ParameterHandler<T> {
    fun parse(parameters: Parameters): T
    val translator: OpenAPIPathSegmentTranslator
}

object UnitParameterHandler: ParameterHandler<Unit> {
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

class PrimitiveParameterParser<T>(override val key: String, val parse: (String?) -> T) : ParameterParser {
    override fun parse(parameters: Parameters): T {
        return parse(parameters[key])
    }
}

private fun <T> genPathParseFunc(key: String, style: PathParamStyle, parse: (String?) -> T): (String?) -> T {
    return when (style) {
        PathParamStyle.simple -> parse
        PathParamStyle.label -> ({ parse(it?.removePrefix(".")) })
        PathParamStyle.matrix -> ({ parse(it?.removePrefix(";$key=")) })
    }
}

class PrimitivePathParameterParserWrapper<T>(
    val parser: PrimitiveParameterParser<T>,
    override val style: PathParamStyle,
    override val explode: Boolean
) : PathParameterParser {
    override val key: String
        get() = parser.key
    override val translation: PathParameterTranslation = PathParameterTranslation(key, style, explode)

    private val parseFunc = genPathParseFunc(key, style, parser.parse)

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }
}

class PrimitiveQueryParameterParserWrapper<T>(
    val parser: PrimitiveParameterParser<T>,
    style: QueryParamStyle,
    override val explode: Boolean
) : QueryParameterParser {
    init {
        if (style != QueryParamStyle.form)
            log.warn("Using non-form style for primitive type, it is undefined in the OpenAPI standard, reverting to form style")
    }

    override val key: String
        get() = parser.key
    override val style: QueryParamStyle = QueryParamStyle.form
    override val translation: QueryParameterTranslation = QueryParameterTranslation(key, this.style, explode)
    override fun parse(parameters: Parameters): Any? {
        return parser.parse(parameters[key])
    }
}

class EnumQueryParameterParser(info: ParameterInfo, val enumMap: Map<String, *>, val nullable: Boolean): QueryParameterParser {
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

class EnumPathParameterParser(info: ParameterInfo, val enumMap: Map<String, *>, val nullable: Boolean): PathParameterParser {
    override val key: String = info.key
    override val style: PathParamStyle = info.pathAnnotation!!.style
    override val explode: Boolean = info.queryAnnotation!!.explode
    override val translation: PathParameterTranslation = PathParameterTranslation(key, style, explode)

    private fun parse(parameter: String?): Any? {
        return parameter?.let { enumMap[it] }
    }

    private val parseFunc = genPathParseFunc(key, style, ::parse)

    override fun parse(parameters: Parameters): Any? {
        return parseFunc(parameters[key])
    }
}

//class CollectionPathParameterParser<T, A>(info: ParameterInfo, val cvt: (List<T>)->A): PathParameterParser {
//    override val key: String = info.key
//    override val style: PathParamStyle = info.pathAnnotation!!.style
//    override val explode: Boolean = info.queryAnnotation!!.explode
//    override val translation: PathParameterTranslation = PathParameterTranslation(key, style, explode)
//
//    private fun parse(parameter: String?): Any? {
//        return parameter?.let { enumMap[it] }
//    }
//
//    private val parseFunc = when (style) {
//        PathParamStyle.simple -> {
//
//        }
//        PathParamStyle.label -> {
//
//        }
//        PathParamStyle.matrix -> {
//
//        }
//    }
//
//    override fun parse(parameters: Parameters): Any? {
//        return parseFunc(parameters[key])
//    }
//}



inline fun <reified T> primitive(noinline cvt: (String?) -> T): Pair<KType, (String) -> PrimitiveParameterParser<T>> {
    return getKType<T>() to { key -> PrimitiveParameterParser(key, cvt) }
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
    primitive { it?.toLongOrNull()?.let(::Date) ?: it?.let(dateFormat::parse) ?: Date() },
    primitive { it?.toLongOrNull()?.let(::Date) ?: it?.let(dateFormat::parse) },
    primitive { it?.toLongOrNull()?.let(Instant::ofEpochMilli) ?: it?.let(Instant::parse) ?: Instant.now() },
    primitive { it?.toLongOrNull()?.let(Instant::ofEpochMilli) ?: it?.let(Instant::parse) },
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
        return constructor.callBy(parsers.mapValues { it.value.parse(parameters) })
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
                PrimitivePathParameterParserWrapper(it(key), info.pathAnnotation.style, info.pathAnnotation.explode)
            } else {
                PrimitiveQueryParameterParserWrapper(it(key), info.queryAnnotation!!.style, info.queryAnnotation.explode)
            }
        } ?: makeParameterParser(type, info)
    }
    return ModularParameterHander(parsers, constructor)
}


data class ParameterInfo(val key: String, val pathAnnotation: PathParam? = null, val queryAnnotation: QueryParam? = null) {
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
        clazz.isSubclassOf(List::class) || (jclazz.let { it.isArray && !it.componentType.isPrimitive }) -> {
            makeListParameterParser(type, info)
        }
        jclazz.isArray -> makeArrayParameterParser(type, info)
        clazz.isSubclassOf(Map::class) -> makeMapParameterParser(type, info)
        else -> makeObjectParameterParser(type, info)
    }
}

private fun makeEnumParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    return if (info.pathAnnotation != null) {
        EnumPathParameterParser(info, type.jvmErasure.java.enumConstants.associateBy { (it as Enum<*>).name }, type.isMarkedNullable)
    } else {
        EnumQueryParameterParser(info, type.jvmErasure.java.enumConstants.associateBy { (it as Enum<*>).name }, type.isMarkedNullable)
    }
}

private fun makeListParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val contentType = type.arguments[0].type!!
    return TODO("Implement $type")
}

private fun makeArrayParameterParser(type: KType, info: ParameterInfo): ParameterParser {
    val contentType = type.jvmErasure.java.componentType.toKType()
    return TODO("Implement $type")
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
