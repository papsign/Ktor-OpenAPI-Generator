package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject.DeepBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
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
        FormBuilderFactory.testSelector(expected, key, parse, true)
    }
}
