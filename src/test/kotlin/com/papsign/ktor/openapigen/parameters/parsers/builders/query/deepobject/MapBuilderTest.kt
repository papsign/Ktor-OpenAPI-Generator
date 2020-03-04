package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class MapBuilderTest {

    @Test
    fun testString() {
        val key = "key"
        val expected = mapOf(
            "test" to "test"
        )
        val parse = mapOf(
            "$key[test]" to listOf("test")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun testFloat() {
        val key = "key"
        val expected = mapOf(
            12.5f to "test"
        )
        val parse = mapOf(
            "$key[12.5]" to listOf("test")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun testBoolean() {
        val key = "key"
        val expected = mapOf(
            true to "test"
        )
        val parse = mapOf(
            "$key[true]" to listOf("test")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }
}
