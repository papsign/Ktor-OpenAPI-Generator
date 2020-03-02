package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class EnumBuilderTest {

    enum class TestEnum {
        A, B, C
    }

    @Test
    fun testEnum() {
        val key = "key"
        val expected = TestEnum.B
        val parse = mapOf(
            key to listOf("B")
        )
        EnumBuilder.testSelector(expected, key, parse, true)
    }
}
