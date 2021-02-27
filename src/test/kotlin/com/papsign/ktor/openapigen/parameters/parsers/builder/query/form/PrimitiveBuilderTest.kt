package com.papsign.ktor.openapigen.parameters.parsers.builder.query.form

import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.testSelector
import com.papsign.ktor.openapigen.parameters.parsers.testSelectorFails
import org.junit.Test
import java.time.*

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

    @Test
    fun testLocalDate() {
        val key = "key"
        val expected = LocalDate.of(2021, Month.FEBRUARY, 27)
        val parse = mapOf(
            key to listOf("2021-02-27")
        )
        FormBuilderFactory.testSelector(expected, key, parse, true)
        FormBuilderFactory.testSelectorFails<LocalDate>(key, mapOf(key to listOf("")), true)
    }

    @Test
    fun testLocalTime() {
        val key = "key"

        val cases = listOf(
            "10:30:00" to LocalTime.of(10, 30, 0),
            "10:30" to LocalTime.of(10, 30, 0),
            "10:30:00.1" to LocalTime.of(10, 30, 0, 100_000_000),
            "10:30:00.12" to LocalTime.of(10, 30, 0, 120_000_000),
            "10:30:00.123" to LocalTime.of(10, 30, 0, 123_000_000),
            "10:30:00.12345" to LocalTime.of(10, 30, 0, 123_450_000),
            "10:30:00.123456789" to LocalTime.of(10, 30, 0, 123_456_789)
        )
        cases.forEach {
            val expected = it.second
            val parse = mapOf(
                key to listOf(it.first)
            )
            FormBuilderFactory.testSelector(expected, key, parse, true)
        }
        val failCases = listOf(
            "",
            "10"
        )
        failCases.forEach {
            FormBuilderFactory.testSelectorFails<LocalDateTime>(key, mapOf(key to listOf(it)), true)
        }
    }

    @Test
    fun testOffsetTime() {
        val key = "key"

        val cases = listOf(
            "10:30:00+03:00" to OffsetTime.of(LocalTime.of(10, 30, 0), ZoneOffset.ofHours(3)),
            "10:30+03:00" to OffsetTime.of(LocalTime.of(10, 30, 0), ZoneOffset.ofHours(3)),
            "10:30Z" to OffsetTime.of(LocalTime.of(10, 30, 0), ZoneOffset.UTC),
            "10:30:00.1Z" to OffsetTime.of(LocalTime.of(10, 30, 0, 100_000_000), ZoneOffset.UTC),
            "10:30:00.12Z" to OffsetTime.of(LocalTime.of(10, 30, 0, 120_000_000), ZoneOffset.UTC),
            "10:30:00.123Z" to OffsetTime.of(LocalTime.of(10, 30, 0, 123_000_000), ZoneOffset.UTC),
            "10:30:00.12345Z" to OffsetTime.of(LocalTime.of(10, 30, 0, 123_450_000), ZoneOffset.UTC),
            "10:30:00.123456789Z" to OffsetTime.of(LocalTime.of(10, 30, 0, 123_456_789), ZoneOffset.UTC)
        )
        cases.forEach {
            val expected = it.second
            val parse = mapOf(
                key to listOf(it.first)
            )
            FormBuilderFactory.testSelector(expected, key, parse, true)
        }
        val failCases = listOf(
            "",
            "10",
            "10:30",
            "10:30:00"
        )
        failCases.forEach {
            FormBuilderFactory.testSelectorFails<LocalDateTime>(key, mapOf(key to listOf(it)), true)
        }
    }

    @Test
    fun testLocalDateTime() {
        val key = "key"

        val baseDate = LocalDate.of(2021, Month.FEBRUARY, 27)

        val cases = listOf(
            "2021-02-27T10:30:00" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0)),
            "2021-02-27 10:30:00" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0)),
            "2021-02-27 10:30" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0)),
            "2021-02-27 10:30:00.1" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0, 100_000_000)),
            "2021-02-27 10:30:00.12" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0, 120_000_000)),
            "2021-02-27 10:30:00.123" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0, 123_000_000)),
            "2021-02-27 10:30:00.12345" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0, 123_450_000)),
            "2021-02-27 10:30:00.123456789" to LocalDateTime.of(baseDate, LocalTime.of(10, 30, 0, 123_456_789))
        )
        cases.forEach {
            val expected = it.second
            val parse = mapOf(
                key to listOf(it.first)
            )
            FormBuilderFactory.testSelector(expected, key, parse, true)
        }
        val failCases = listOf(
            "",
            "2021-02-27",
            "2021-02-27 ",
            "2021-02-27T",
            "2021-02-27 10",
            "2021-02-27T10"
        )
        failCases.forEach {
            FormBuilderFactory.testSelectorFails<LocalDateTime>(key, mapOf(key to listOf(it)), true)
        }

    }

    @Test
    fun testOffsetDateTime() {
        val key = "key"

        val baseDateTime = LocalDateTime.of(LocalDate.of(2021, Month.FEBRUARY, 27), LocalTime.of(10, 30, 0))

        val cases = listOf(
            "2021-02-27T10:30:00+03:00" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(3)),
            "2021-02-27 10:30:00+03:00" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(3)),
            "2021-02-27 10:30:00+03" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(3)),
            "2021-02-27 10:30:00Z" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(0)),
            "2021-02-27 10:30:00+18:00" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(18)),
            "2021-02-27 10:30:00-18:00" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(-18)),

            "2021-02-27 10:30:00.1Z" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(100_000_000),
            "2021-02-27 10:30:00.12Z" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(120_000_000),
            "2021-02-27 10:30:00.123Z" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(123_000_000),
            "2021-02-27 10:30:00.12345Z" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(123_450_000),
            "2021-02-27 10:30:00.123456789Z" to OffsetDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(123_456_789)
        )
        cases.forEach {
            val expected = it.second
            val parse = mapOf(
                key to listOf(it.first)
            )
            FormBuilderFactory.testSelector(expected, key, parse, true)
        }

        val failCases = listOf(
            "",
            "2021-02-27",
            "2021-02-27 ",
            "2021-02-27T",
            "2021-02-27 10",
            "2021-02-27 10:30",
            "2021-02-27 10:30:00",
            "2021-02-27 10:30:00+99:00",
            "2021-02-27 10:30:00+24:00",
            "2021-02-27 10:30:00+19:00",
            "2021-02-27 10:30:00-19:00",
            "2021-02-27 10:30:00 03:00"
        )

        failCases.forEach {
            FormBuilderFactory.testSelectorFails<OffsetDateTime>(key, mapOf(key to listOf(it)), true)
        }
    }

    @Test
    fun testZonedDateTime() {
        val key = "key"

        val baseDateTime = LocalDateTime.of(LocalDate.of(2021, Month.FEBRUARY, 27), LocalTime.of(10, 30, 0))

        val cases = listOf(
            "2021-02-27T10:30:00+03:00" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(3)),
            "2021-02-27T10:30:00+03:00[Europe/Moscow]" to ZonedDateTime.of(baseDateTime, ZoneId.of("Europe/Moscow")),
            "2021-02-27T10:30:00+03[Europe/Moscow]" to ZonedDateTime.of(baseDateTime, ZoneId.of("Europe/Moscow")),
            "2021-02-27T10:30:00[Europe/Moscow]" to ZonedDateTime.of(baseDateTime, ZoneId.of("Europe/Moscow")),
            "2021-02-27 10:30:00+03:00" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(3)),
            "2021-02-27 10:30:00+03" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(3)),
            "2021-02-27 10:30:00Z" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(0)),
            "2021-02-27 10:30:00+18:00" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(18)),
            "2021-02-27 10:30:00-18:00" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(-18)),

            "2021-02-27 10:30:00.1Z" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(100_000_000),
            "2021-02-27 10:30:00.12Z" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(120_000_000),
            "2021-02-27 10:30:00.123Z" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(123_000_000),
            "2021-02-27 10:30:00.12345Z" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(123_450_000),
            "2021-02-27 10:30:00.123456789Z" to ZonedDateTime.of(baseDateTime, ZoneOffset.ofHours(0)).plusNanos(123_456_789)
        )
        cases.forEach {
            val expected = it.second
            val parse = mapOf(
                key to listOf(it.first)
            )
            FormBuilderFactory.testSelector(expected, key, parse, true)
        }

        val failCases = listOf(
            "",
            "2021-02-27",
            "2021-02-27 ",
            "2021-02-27T",
            "2021-02-27 10",
            "2021-02-27 10:30",
            "2021-02-27 10:30:00",
            "2021-02-27 10:30:00[NotExisting/Timezone]",
            "2021-02-27 10:30:00+99:00[NotExisting/Timezone]",
            "2021-02-27 10:30:00+99:00",
            "2021-02-27 10:30:00+24:00",
            "2021-02-27 10:30:00+19:00",
            "2021-02-27 10:30:00-19:00",
            "2021-02-27 10:30:00 03:00"
        )

        failCases.forEach {
            FormBuilderFactory.testSelectorFails<ZonedDateTime>(key, mapOf(key to listOf(it)), true)
        }
    }

    @Test
    fun testInstant() {
        val key = "key"

        val instant = OffsetDateTime.of(LocalDate.of(2021, Month.FEBRUARY, 27), LocalTime.of(10, 30, 0), ZoneOffset.UTC).toInstant()
        val epochMillis = instant.toEpochMilli()

        val cases = listOf(
            "2021-02-27T10:30:00Z" to instant,
            "2021-02-27 10:30:00Z" to instant,
            "2021-02-27 10:30:00+00:00" to instant,
            "2021-02-27T13:30:00+03:00" to instant,
            "2021-02-27 13:30:00+03:00" to instant,
            "$epochMillis" to instant,

            "2021-02-27 10:30:00.1Z" to instant.plusNanos(100_000_000),
            "2021-02-27 10:30:00.12Z" to instant.plusNanos(120_000_000),
            "2021-02-27 10:30:00.123Z" to instant.plusNanos(123_000_000),
            "2021-02-27 10:30:00.12345Z" to instant.plusNanos(123_450_000),
            "2021-02-27 10:30:00.123456789Z" to instant.plusNanos(123_456_789)
        )
        cases.forEach {
            val expected = it.second
            val parse = mapOf(
                key to listOf(it.first)
            )
            FormBuilderFactory.testSelector(expected, key, parse, true)
        }

        val failCases = listOf(
            "",
            "2021-02-27",
            "2021-02-27 10:30:00",
            "2021-02-27T10:30:00"
        )

        failCases.forEach {
            FormBuilderFactory.testSelectorFails<Instant>(key, mapOf(key to listOf(it)), true)
        }
    }
}
