package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.toStringWithIgnoreCaseFlag
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
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
            key.toStringWithIgnoreCaseFlag() to listOf("test,test")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false)
    }

    @Test
    fun testFloat() {
        val key = "key"
        val expected = mapOf(
            12.5f to "test"
        )
        val parse = mapOf(
            key.toStringWithIgnoreCaseFlag() to listOf("12.5,test")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false)
    }

    @Test
    fun testBoolean() {
        val key = "key"
        val expected = mapOf(
            true to "test"
        )
        val parse = mapOf(
            key.toStringWithIgnoreCaseFlag() to listOf("true,test")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false)
    }
}
