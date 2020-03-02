package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.parameters.parsers.deepobject.DeepBuilder
import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import kotlin.reflect.full.isSuperclassOf
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

inline fun <reified T> BuilderSelector<*>.testSelector(
    expect: T,
    key: String,
    parseData: Map<String, List<String>>,
    explode: Boolean,
    equals: (expected: T, actual: T) -> Boolean = { a, b -> a == b }
) {
    val type = getKType<T>()
    assert(canHandle(type)) { "BuilderSelector ${javaClass.simpleName} could not handle type $type" }
    val builder = create(type, explode)
    assertNotNull(builder, "BuilderSelector ${javaClass.simpleName} could not be generated for type $type")
    val actual = builder.build(key, parseData)
    if (actual != null) {
        assert(T::class.isSuperclassOf(actual::class)) { "Actual class ${actual.javaClass.simpleName} must be subclass of ${T::class.java.simpleName}" }
    }
    assert(equals(expect, actual as T)) { "Expected $expect, Actual: $actual" }
}
