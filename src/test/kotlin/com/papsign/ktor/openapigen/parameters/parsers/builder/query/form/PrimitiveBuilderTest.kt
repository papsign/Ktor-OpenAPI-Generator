package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
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
        FormBuilderFactory.testSelector(expected, key, parse, true)
    }
}
