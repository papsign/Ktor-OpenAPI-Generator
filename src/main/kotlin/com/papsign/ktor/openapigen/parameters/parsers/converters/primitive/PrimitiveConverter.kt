package com.papsign.ktor.openapigen.parameters.parsers.converters.primitive

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.parameters.parsers.converters.Converter
import com.papsign.ktor.openapigen.parameters.parsers.converters.ConverterSelector
import com.papsign.ktor.openapigen.parameters.util.localDateTimeFormatter
import com.papsign.ktor.openapigen.parameters.util.offsetDateTimeFormatter
import com.papsign.ktor.openapigen.parameters.util.zonedDateTimeFormatter
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.reflect.KType

object PrimitiveConverter : ConverterSelector {

    private inline fun <reified T> primitive(noinline cvt: (String) -> T): Pair<KType, Converter> {
        return getKType<T>() to object : Converter {
            override fun convert(value: String): Any? = cvt(value)
        }
    }

//    private val dateFormat = SimpleDateFormat()

    private val primitiveParsers = mapOf(
        primitive { it.toByteOrNull() ?: 0 },
        primitive { it.toByteOrNull() },
        primitive { it.toShortOrNull() ?: 0 },
        primitive { it.toShortOrNull() },
        primitive { it.toIntOrNull() ?: 0 },
        primitive { it.toIntOrNull() },
        primitive {
            it.toLongOrNull() ?: 0
        },
        primitive { it.toLongOrNull() },
        primitive {
            it.toBigIntegerOrNull() ?: BigInteger.ZERO
        },
        primitive { it.toBigIntegerOrNull() },
        primitive {
            it.toBigDecimalOrNull() ?: BigDecimal.ZERO
        },
        primitive { it.toBigDecimalOrNull() },
        primitive { it.toFloatOrNull() ?: 0f },
        primitive { it.toFloatOrNull() },
        primitive {
            it.toDoubleOrNull() ?: 0.0
        },
        primitive { it.toDoubleOrNull() },
        primitive { it.toBoolean() },
        primitive<Boolean?> { it.toBoolean() },
        // removed temporarily because behavior may not be standard or expected

        primitive {
            LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
        },
        primitive {
            try {
                LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
            } catch(e: DateTimeParseException) {
                null
            }
        },

        primitive {
            LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
        },
        primitive {
            try {
                LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
            } catch(e: DateTimeParseException) {
                null
            }
        },

        primitive {
            OffsetTime.parse(it, DateTimeFormatter.ISO_OFFSET_TIME)
        },
        primitive {
            try {
                OffsetTime.parse(it, DateTimeFormatter.ISO_OFFSET_TIME)
            } catch(e: DateTimeParseException) {
                null
            }
        },

        primitive {
            LocalDateTime.parse(it, localDateTimeFormatter)
        },
        primitive {
            try {
                LocalDateTime.parse(it, localDateTimeFormatter)
            } catch(e: DateTimeParseException) {
                null
            }
        },

        primitive {
            OffsetDateTime.parse(it, offsetDateTimeFormatter)
        },
        primitive {
            try {
                OffsetDateTime.parse(it, offsetDateTimeFormatter)
            } catch(e: DateTimeParseException) {
                null
            }
        },

        primitive {
            ZonedDateTime.parse(it, zonedDateTimeFormatter)
        },
        primitive {
            try {
                ZonedDateTime.parse(it, zonedDateTimeFormatter)
            } catch(e: DateTimeParseException) {
                null
            }
        },

        primitive { it.toLongOrNull()?.let(Instant::ofEpochMilli) ?: Instant.from(offsetDateTimeFormatter.parse(it)) },
        primitive {
            try {
                it.toLongOrNull()?.let(Instant::ofEpochMilli) ?: Instant.from(offsetDateTimeFormatter.parse(it))
            } catch(e: DateTimeParseException) {
                null
            }
        },

//        primitive { it?.toLongOrNull()?.let(::Date) ?: it?.let(dateFormat::parse) ?: Date() },
//        primitive { it?.toLongOrNull()?.let(::Date) ?: it?.let(dateFormat::parse) },

        primitive {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            } ?: UUID(0, 0)
        },
        primitive {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        },
        primitive { it },
        primitive<String?> { it }
    )

    override fun canHandle(type: KType): Boolean {
        return primitiveParsers.containsKey(type)
    }

    override fun create(type: KType): Converter {
        return primitiveParsers[type] ?: error("could not find Converter for primitive type $type")
    }
}
