package com.papsign.ktor.openapigen.parameters.util

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

private fun baseDateTimeFormatterBuilder() = DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM-dd['T'][ ]HH:mm[:ss]")
    .optionalStart()
    .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
    .optionalEnd()

val localDateTimeFormatter: DateTimeFormatter = baseDateTimeFormatterBuilder().toFormatter()

val offsetDateTimeFormatter: DateTimeFormatter = baseDateTimeFormatterBuilder()
    .appendPattern("[xxx][xx][X]")
    .toFormatter()

val zonedDateTimeFormatter: DateTimeFormatter = baseDateTimeFormatterBuilder()
    .appendPattern("[xxx][xx][X]")
    .optionalStart()
    .appendLiteral('[')
    .optionalEnd()
    .optionalStart()
    .appendZoneId()
    .optionalEnd()
    .optionalStart()
    .appendLiteral(']')
    .optionalEnd()
    .toFormatter()
