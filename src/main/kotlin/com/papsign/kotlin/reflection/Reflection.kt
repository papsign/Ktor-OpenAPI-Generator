package com.papsign.kotlin.reflection

import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

val unitKType = getKType<Unit>()

inline fun <reified T> isNullable(): Boolean {
    return null is T
}

inline fun <reified T> getKType(): KType {
    return object : SuperTypeTokenHolder<T>() { }.getKTypeImpl().withNullability(isNullable<T>())
}

// TODO: Lobby for a real kotlin ::type for types because the status quo sucks

@Suppress("unused")
open class SuperTypeTokenHolder<T>

fun SuperTypeTokenHolder<*>.getKTypeImpl(): KType =
        javaClass.genericSuperclass.toKType().arguments.single().type!!

fun KClass<*>.toInvariantFlexibleProjection(arguments: List<KTypeProjection> = emptyList()): KTypeProjection {
    // TODO: there should be an API in kotlin-reflect which creates KType instances corresponding to flexible types
    // Currently we always produce a non-null type, which is obviously wrong
    val args = when {
        java.isArray && java.componentType.isPrimitive -> listOf()
        java.isArray -> listOf(java.componentType.kotlin.toInvariantFlexibleProjection())
        else -> arguments
    }
    return KTypeProjection.invariant(createType(args, nullable = false))
}

fun Type.toKTypeProjection(): KTypeProjection = when (this) {
    is Class<*> -> this.kotlin.toInvariantFlexibleProjection()
    is ParameterizedType -> {
        val erasure = (rawType as Class<*>).kotlin
        erasure.toInvariantFlexibleProjection((erasure.typeParameters.zip(actualTypeArguments).map { (parameter, argument) ->
            val projection = argument.toKTypeProjection()
            projection.takeIf {
                // Get rid of use-site projections on arguments, where the corresponding parameters already have a declaration-site projection
                parameter.variance == KVariance.INVARIANT || parameter.variance != projection.variance
            } ?: KTypeProjection.invariant(projection.type!!)
        }))
    }
    is WildcardType -> when {
        lowerBounds.isNotEmpty() -> KTypeProjection.contravariant(lowerBounds.single().toKType())
        upperBounds.isNotEmpty() -> KTypeProjection.covariant(upperBounds.single().toKType())
        // This looks impossible to obtain through Java reflection API, but someone may construct and pass such an instance here anyway
        else -> KTypeProjection.STAR
    }
    is GenericArrayType -> Array<Any>::class.toInvariantFlexibleProjection(listOf(genericComponentType.toKTypeProjection()))
    is TypeVariable<*> -> TODO() // TODO
    else -> throw IllegalArgumentException("Unsupported type: $this")
}

fun Type.toKType(): KType = toKTypeProjection().type!!

private val subTypeMap = HashMap<KType, Set<KType>>()

private fun getTypeSubTypes(type: KType): Set<KType> {
    return subTypeMap.getOrPut(type) {
        val clazz = type.jvmErasure
        val jclazz = clazz.java
        when {
            jclazz.isEnum -> getEnumSubTypes(type)
            clazz.isSubclassOf(List::class) || (jclazz.let { it.isArray && !it.componentType.isPrimitive }) -> {
                getListSubTypes(type)
            }
            jclazz.isArray -> getArraySubTypes(type)
            clazz.isSubclassOf(Map::class) -> makeMapSchema(type)
            else -> getObjectSubTypes(type)
        }.flatMap { getTypeSubTypes(it) }.plus(type).toSet()
    }
}

private fun getEnumSubTypes(type: KType): Set<KType> {
    return setOf()
}

private fun getListSubTypes(type: KType): Set<KType> {
    return setOf(type.arguments[0].type!!)
}

private fun getArraySubTypes(type: KType): Set<KType> {
    return setOf(type.jvmErasure.java.componentType.toKType())
}

private fun getObjectSubTypes(type: KType): Set<KType> {

    val props = type.jvmErasure.declaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }
    return props.map {
        it.returnType
    }.toSet()
}


private fun makeMapSchema(type: KType): Set<KType> {
    return setOf(type.arguments[1].type!!, getKType<String>())
}

fun KType.allTypes(): Set<KType> {
    return getTypeSubTypes(this)
}

fun KType.getObjectSubtypes(): Set<KType> {
    return getObjectSubTypes(this)
}

// --- Usage example ---

//fun main(args: Array<String>) {
//    println(getKType<List<Map<String, Array<Double>>>>())
//    println(getKType<List<*>>())
//    println(getKType<Array<*>>())
//    println(getKType<Array<Int?>?>())
//    println(getKType<Array<Array<String>>>())
//    println(getKType<Unit>())
//}