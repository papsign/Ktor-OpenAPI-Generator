package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.parameters.ParameterStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderParameters
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelector
import java.lang.reflect.Array
import kotlin.reflect.full.isSuperclassOf
import kotlin.test.assertFails
import kotlin.test.assertNotNull

inline fun <reified T> BuilderSelector<*>.testSelector(
    expect: T,
    key: String,
    parseData: BuilderParameters,
    explode: Boolean,
    equals: (expected: T, actual: T) -> Boolean = { a, b -> a == b }
) {
    val type = getKType<T>()
    assert(canHandle(type, explode)) { "BuilderSelector ${javaClass.simpleName} could not handle type $type" }
    val builder = create(type, explode)
    assertNotNull(builder, "BuilderSelector ${javaClass.simpleName} could not be generated for type $type")
    val actual = builder.build(key, parseData)
    if (actual != null) {
        assert(T::class.isSuperclassOf(actual::class)) { "Actual class ${actual.javaClass.simpleName} must be subclass of ${T::class.java.simpleName}" }
    }
    assert(equals(expect, actual as T)) { "Expected $expect, Actual: $actual" }
}


fun toStr(any: Any?): String {
    return if (any != null && any.javaClass.isArray) {
        (0 until Array.getLength(any)).map {
            Array.get(any, it)
        }.toString()
    } else {
        any.toString()
    }
}

inline fun <reified T, B: Builder<S>, S> BuilderFactory<B, S>.testSelector(
    expect: T,
    key: String,
    parseData: BuilderParameters,
    explode: Boolean,
    equals: (expected: T, actual: T) -> Boolean = { a, b -> a == b }
) where S: ParameterStyle<S>, S: Enum<S> {

    val type = getKType<T>()
    val builder = buildBuilder(type, explode)
    assertNotNull(builder, "BuilderSelector ${javaClass.simpleName} could not be generated for type $type")
    val actual = builder.build(key, parseData)
    println("$expect = $actual")
    if (actual != null) {
        assert(T::class.isSuperclassOf(actual::class)) { "Actual class ${actual.javaClass.simpleName} from builder ${builder.javaClass.simpleName} must be subclass of ${T::class.java.simpleName}" }
    }
    assert(equals(expect, actual as T)) { "Expected ${toStr(expect)}, Actual: ${toStr(actual)}" }
}


inline fun <reified T> BuilderFactory<*, *>.testSelectorFails(
    key: String,
    parseData: BuilderParameters,
    explode: Boolean
) {
    val type = getKType<T>()
    val builder = buildBuilder(type, explode)
    assertNotNull(builder, "BuilderSelector ${javaClass.simpleName} could not be generated for type $type")
    assertFails("Expected to fail $parseData") {
        builder.build(key, parseData)
    }
}

