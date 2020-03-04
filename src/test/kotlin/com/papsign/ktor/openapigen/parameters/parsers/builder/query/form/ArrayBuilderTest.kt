package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class ArrayBuilderTest {

    @Test
    fun testFloatArray() {
        val key = "key"
        val expected = floatArrayOf(1f, 2f, 2.5f)
        val parse = mapOf(
            key to listOf("1,2,2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false) { a, b -> a.contentEquals(b) }
    }

    @Test
    fun testNullableFloatArray() {
        val key = "key"
        val expected = arrayOf(1f, null, 2.5f)
        val parse = mapOf(
            key to listOf("1,,2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false) { a, b -> a.contentEquals(b) }
    }

    @Test
    fun testFloatArrayExploded() {
        val key = "key"
        val expected = floatArrayOf(1f, 2f, 2.5f)
        val parse = mapOf(
           key to listOf("1","2","2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, true) { a, b -> a.contentEquals(b) }
    }

    @Test
    fun testNullableFloatArrayExploded() {
        val key = "key"
        val expected = arrayOf(1f, null, 2.5f)
        val parse = mapOf(
            key to listOf("1", "null", "2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, true) { a, b -> a.contentEquals(b) }
    }
}
