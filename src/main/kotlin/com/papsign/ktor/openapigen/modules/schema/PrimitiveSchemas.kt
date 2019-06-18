package com.papsign.ktor.openapigen.modules.schema

import com.papsign.kotlin.reflection.isNullable
import com.papsign.ktor.openapigen.LinkedHashSchemaMap
import com.papsign.ktor.openapigen.MutableSchemaMap
import com.papsign.ktor.openapigen.openapi.DataFormat
import com.papsign.ktor.openapigen.openapi.DataType
import com.papsign.ktor.openapigen.openapi.Schema.SchemaLitteral
import com.papsign.ktor.openapigen.put
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.*
import kotlin.reflect.KType

class PrimitiveSchemas(val namer: SchemaNamer) : PartialSchemaRegistrar {

    override fun get(type: KType): NamedSchema? {
        return basicSchemas[type]?.let { NamedSchema(namer[type], it) }
    }

    companion object {
        private val basicSchemas = LinkedHashSchemaMap().apply {
            registerSchema<Boolean>(DataType.boolean)
            registerSchema<Boolean?>(DataType.boolean)

            registerSchema<Int>(DataType.integer, DataFormat.int32, Int.MIN_VALUE, Int.MAX_VALUE)
            registerSchema<Int?>(DataType.integer, DataFormat.int32, Int.MIN_VALUE, Int.MAX_VALUE)

            registerSchema<Long>(DataType.integer, DataFormat.int64, Long.MIN_VALUE, Long.MAX_VALUE)
            registerSchema<Long?>(DataType.integer, DataFormat.int64, Long.MIN_VALUE, Long.MAX_VALUE)

            registerSchema<BigInteger>(DataType.integer)
            registerSchema<BigInteger?>(DataType.integer)

            registerSchema<String>(DataType.string)
            registerSchema<String?>(DataType.string)

            registerSchema<UUID>(DataType.string, DataFormat.uuid)
            registerSchema<UUID?>(DataType.string, DataFormat.uuid)

            registerSchema<Float>(DataType.number, DataFormat.float)
            registerSchema<Float?>(DataType.number, DataFormat.float)

            registerSchema<Double>(DataType.number, DataFormat.double)
            registerSchema<Double?>(DataType.number, DataFormat.double)

            registerSchema<BigDecimal>(DataType.number)
            registerSchema<BigDecimal?>(DataType.number)

            registerSchema<Instant>(DataType.string, DataFormat.`date-time`)
            registerSchema<Instant?>(DataType.string, DataFormat.`date-time`)

            registerSchema<Date>(DataType.string, DataFormat.`date-time`)
            registerSchema<Date?>(DataType.string, DataFormat.`date-time`)
        }

        inline fun <reified T> MutableSchemaMap.registerSchema(
            type: DataType,
            format: DataFormat? = null,
            minValue: T? = null,
            maxValue: T? = null
        ) {
            put(
                SchemaLitteral(
                    type,
                    format,
                    isNullable<T>(),
                    minValue,
                    maxValue
                )
            )
        }
    }
}