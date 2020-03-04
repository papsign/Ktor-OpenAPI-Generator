package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class PrimitiveBuilderTest {

    @Test
    fun testFloat() {
        val key = "key"
        val expected = 1f
        val parse = mapOf(
            key to listOf("1")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }
}
