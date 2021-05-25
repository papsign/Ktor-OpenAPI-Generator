package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.builders.toStringWithIgnoreCaseFlag
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class ListBuilderTest {

    @Test
    fun testFloatList() {
        val key = "key"
        val expected = listOf(1f, 2f, 2.5f)
        val parse = mapOf(
            "$key[0]".toStringWithIgnoreCaseFlag() to listOf("1"),
            "$key[1]".toStringWithIgnoreCaseFlag() to listOf("2"),
            "$key[2]".toStringWithIgnoreCaseFlag() to listOf("2.5")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun testNullableFloatList() {
        val key = "key"
        val expected = listOf(1f, null, 2.5f)
        val parse = mapOf(
            "$key[0]".toStringWithIgnoreCaseFlag() to listOf("1"),
            "$key[2]".toStringWithIgnoreCaseFlag() to listOf("2.5")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }
}
