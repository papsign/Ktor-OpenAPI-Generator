package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class ListBuilderTest {

    @Test
    fun testFloatList() {
        val key = "key"
        val expected = listOf(1f, 2f, 2.5f)
        val parse = mapOf(
            key to listOf("1,2,2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false)
    }

    @Test
    fun testNullableFloatList() {
        val key = "key"
        val expected = listOf(1f, null, 2.5f)
        val parse = mapOf(
           key to listOf("1,null,2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, false)
    }

    @Test
    fun testFloatListExploded() {
        val key = "key"
        val expected = listOf(1f, 2f, 2.5f)
        val parse = mapOf(
            key to listOf("1","2","2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun testNullableFloatListExploded() {
        val key = "key"
        val expected = listOf(1f, null, 2.5f)
        val parse = mapOf(
            key to listOf("1","","2.5")
        )
        FormBuilderFactory.testSelector(expected, key, parse, true)
    }
}
