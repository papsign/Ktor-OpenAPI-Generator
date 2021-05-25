package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.builders.toStringWithIgnoreCaseFlag
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class ArrayBuilderTest {

    @Test
    fun testFloatArray() {
        val key = "key"
        val expected = floatArrayOf(1f, 2f, 2.5f)
        val parse = mapOf(
            "$key[0]".toStringWithIgnoreCaseFlag() to listOf("1"),
            "$key[1]".toStringWithIgnoreCaseFlag() to listOf("2"),
            "$key[2]".toStringWithIgnoreCaseFlag() to listOf("2.5")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true) { a, b -> a.contentEquals(b) }
    }

    @Test
    fun testNullableFloatArray() {
        val key = "key"
        val expected = arrayOf(1f, null, 2.5f)
        val parse = mapOf(
            "$key[0]".toStringWithIgnoreCaseFlag() to listOf("1"),
            "$key[2]".toStringWithIgnoreCaseFlag() to listOf("2.5")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true) { a, b -> a.contentEquals(b) }
    }

    data class Test1(val str: String)

    @Test
    fun testNullableObjectArray() {
        val key = "key"
        val expected = arrayOf(Test1("A"), Test1("B"), null)
        val parse = mapOf(
            "$key[0][str]".toStringWithIgnoreCaseFlag() to listOf("A"),
            "$key[1][str]".toStringWithIgnoreCaseFlag() to listOf("B"),
            "$key[2]".toStringWithIgnoreCaseFlag() to listOf()
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true) { a, b -> a.contentEquals(b) }
    }
}
