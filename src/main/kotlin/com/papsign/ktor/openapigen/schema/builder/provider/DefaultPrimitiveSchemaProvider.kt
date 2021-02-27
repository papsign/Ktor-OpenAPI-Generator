package com.papsign.ktor.openapigen.schema.builder.provider

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.model.schema.DataFormat
import com.papsign.ktor.openapigen.model.schema.DataType
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.DefaultOpenAPIModule
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.schema.builder.FinalSchemaBuilder
import com.papsign.ktor.openapigen.schema.builder.SchemaBuilder
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*
import kotlin.reflect.KType

object DefaultPrimitiveSchemaProvider: SchemaBuilderProviderModule, OpenAPIGenModuleExtension, DefaultOpenAPIModule {

    private data class Builder(override val superType: KType, private val model: SchemaModel.SchemaModelLitteral<*>) : SchemaBuilder {
        override fun build(type: KType, builder: FinalSchemaBuilder, finalize: (SchemaModel<*>)->SchemaModel<*>): SchemaModel<*> {
            checkType(type)
            return finalize(model.copy(nullable = type.isMarkedNullable))
        }

        companion object {
            inline operator fun <reified T> invoke(model: SchemaModel.SchemaModelLitteral<T>): Builder {
                return Builder(
                    getKType<T?>(),
                    model
                )
            }

            inline operator fun <reified T> invoke(type: DataType, format: DataFormat? = null, pattern: String? = null, example: T? = null): Builder {
                return Builder(
                    SchemaModel.SchemaModelLitteral<T>(type, format, pattern = pattern, example = example)
                )
            }
        }
    }

    private val builders = listOf(
        Builder<Boolean>(
            DataType.boolean
        ),
        Builder<Int>(
            DataType.integer,
            DataFormat.int32
        ),
        Builder<Long>(
            DataType.integer,
            DataFormat.int64
        ),
        Builder<BigInteger>(
            DataType.integer
        ),
        Builder<String>(
            DataType.string
        ),
        Builder<UUID>(
            DataType.string,
            DataFormat.uuid
        ),
        Builder<Float>(
            DataType.number,
            DataFormat.float
        ),
        Builder<Double>(
            DataType.number,
            DataFormat.double
        ),
        Builder<BigDecimal>(
            DataType.number
        ),
        Builder<LocalDate>(
            DataType.string,
            DataFormat.date,
            example = LocalDate.now()
        ),
        Builder<LocalTime>(
            DataType.string,
            pattern = "HH:mm:ss",
            example = LocalTime.now()
        ),
        Builder<OffsetTime>(
            DataType.string,
            pattern = "HH:mm:ss+XXX",
            example = OffsetTime.now()
        ),
        Builder<LocalDateTime>(
            DataType.string,
            DataFormat.`date-time`,
            example = LocalDateTime.now()
        ),
        Builder<OffsetDateTime>(
            DataType.string,
            DataFormat.`date-time`,
            example = OffsetDateTime.now()
        ),
        Builder<ZonedDateTime>(
            DataType.string,
            DataFormat.`date-time`,
            example = ZonedDateTime.now()
        ),
        Builder<Instant>(
            DataType.string,
            DataFormat.`date-time`,
            example = Instant.now()
        ),
        Builder<Date>(
            DataType.string,
            DataFormat.`date-time`
        ),
        Builder<ByteArray>(
            DataType.string,
            DataFormat.byte
        ),
        Builder<InputStream>(
            DataType.string,
            DataFormat.binary
        )
    )

    override fun provide(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<SchemaBuilder> {
        return builders
    }
}

