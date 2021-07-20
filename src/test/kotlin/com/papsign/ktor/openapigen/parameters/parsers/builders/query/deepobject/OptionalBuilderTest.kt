package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test
import java.util.*

class OptionalBuilderTest {

    @Test
    fun testFilledValue() {
        val key = "key"
        val expected = Optional.of(1)
        val parse = mapOf(
            key to listOf("1")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun testEmptyValue() {
        val key = "key"
        val expected = Optional.empty<Int>()
        val parse = mapOf(
            key to listOf("")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun testExplicitNullValue() {
        val key = "key"
        val expected = Optional.empty<Int>()
        val parse = mapOf(
            key to listOf("null")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }
}
