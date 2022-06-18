package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverter
import com.papsign.ktor.openapigen.parameters.parsers.converters.primitive.PrimitiveConverterFactory
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import com.papsign.ktor.openapigen.parameters.parsers.testSelectorFails
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.full.createType

class InjectBeforeTest {
    @Before
    fun before() {
        PrimitiveConverterFactory.injectConverterBefore(PrimitiveConverter::class, CustomUuidConverter)
    }

    @After
    fun after() {
        PrimitiveConverterFactory.removeConverter(CustomUuidConverter::class)
    }

    @Test
    fun testCustomConverter() {
        val uuid = "4a5e1ba7-c6fe-49de-abf9-d94614ea3bb8"
        val key = "key"
        val expected = UUID.fromString(uuid)
        val parse = mapOf(
            key to listOf(uuid)
        )
        FormBuilderFactory.testSelector(expected, key, parse, true)
        FormBuilderFactory.testSelectorFails<UUID>(key, mapOf(key to listOf("not uuid")), true)
    }
}

class InjectAfterAndRemoveTest {
    @Before
    fun before() {
        PrimitiveConverterFactory.injectConverterAfter(PrimitiveConverter::class, AnyToBooleanConverter)
        PrimitiveConverterFactory.removeConverter(PrimitiveConverter::class)
    }

    @After
    fun after() {
        PrimitiveConverterFactory.injectConverterBefore(AnyToBooleanConverter::class, PrimitiveConverter)
        PrimitiveConverterFactory.removeConverter(AnyToBooleanConverter::class)
    }

    @Test
    fun testConverterRemoval() {
        val values = listOf("random", 1, UUID.randomUUID(), OffsetDateTime.now())

        values.forEach {
            val key = "key"
            val expected = true
            val parse = mapOf(
                key to listOf(it.toString())
            )

            FormBuilderFactory.testSelector(expected, key, parse, true)
        }
    }
}

private object AnyToBooleanConverter : ConverterSelector, Converter {
    override fun convert(value: String): Any = true

    override fun canHandle(type: KType): Boolean = true

    override fun create(type: KType): Converter = this
}

private object CustomUuidConverter : ConverterSelector, Converter {
    override fun convert(value: String): Any = UUID.fromString(value)

    override fun canHandle(type: KType): Boolean = type == UUID::class.createType()

    override fun create(type: KType): Converter =
        if (canHandle(type)) this
        else throw RuntimeException("Cannot create converter that can handle $type")
}
