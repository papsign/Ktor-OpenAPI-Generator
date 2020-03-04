package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import org.junit.Test

class ObjectBuilderTest {

    data class TestClass1(val string: String)

    @Test
    fun test1() {
        val key = "key"
        val expected = TestClass1("test")
        val parse = mapOf(
            "$key[string]" to listOf("test")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    data class TestClass2(val nested: TestClass2Nested)
    data class TestClass2Nested(val string: String, val float: Float)

    @Test
    fun test2() {
        val key = "key"
        val expected = TestClass2(TestClass2Nested("test", 1f))
        val parse = mapOf(
            "$key[nested][string]" to listOf("test"),
            "$key[nested][float]" to listOf("1")
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }


    data class TestClass3(val nested: Map<Boolean, TestClass3>)

    @Test
    fun test3() {
        val key = "key"
        val expected = TestClass3(mapOf(true to TestClass3(mapOf(false to TestClass3(mapOf())))))
        val parse = mapOf<String, List<String>>(
            "$key[nested][true][nested][false][string]" to listOf<String>()
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }

    data class TestClass4(val nested: List<TestClass4>)

    @Test
    fun test4() {
        val key = "key"
        val expected = TestClass4(listOf(TestClass4(listOf())))
        val parse = mapOf<String, List<String>>(
            "$key[nested][0][0][nested]" to listOf<String>()
        )
        DeepBuilderFactory.testSelector(expected, key, parse, true)
    }
}
