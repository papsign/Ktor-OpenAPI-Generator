package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.annotations.type.enum.StrictEnumParsing
import com.papsign.ktor.openapigen.exceptions.OpenAPIBadContentException
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EnumBuilderTest {

    enum class TestEnum {
        A, B, C
    }

    @StrictEnumParsing
    enum class StrictTestEnum {
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

    @Test
    fun testStrictEnum() {
        val key = "key"
        val expected = StrictTestEnum.B
        val parse = mapOf(
            key to listOf("B")
        )
        FormBuilderFactory.testSelector(expected, key, parse, true)
    }

    @Test
    fun `should NOT throw on enum value outside of enum without StrictParsing and return null`() {
        val type = getKType<TestEnum>()
        val builder = assertNotNull(FormBuilderFactory.buildBuilder(type, true))
        assertNull(builder.build("key", mapOf("key" to listOf("XXX"))))
    }

    @Test
    fun `should throw on enum value outside of enum with StrictParsing`() {
        val type = getKType<StrictTestEnum>()
        val builder = assertNotNull(FormBuilderFactory.buildBuilder(type, true))
        assertFailsWith<OpenAPIBadContentException> {
            builder.build("key", mapOf("key" to listOf("XXX")))
        }
    }
}
