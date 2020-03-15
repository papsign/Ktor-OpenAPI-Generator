package com.papsign.ktor.openapigen.schema.builder.provider

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.model.schema.DataFormat
import com.papsign.ktor.openapigen.model.schema.DataType
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.schema.builder.SchemaBuilder
import java.io.InputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.*
import kotlin.reflect.KType

object DefaultPrimitiveSchemaProvider: SchemaBuilderProviderModule, OpenAPIGenModuleExtension {

    private data class Builder(override val superType: KType, private val model: SchemaModel.SchemaModelLitteral<*>) :
        SchemaBuilder {
        override fun build(type: KType, builder: SchemaBuilder): SchemaModel<*> {
            checkType(type)
            return model.copy(nullable = type.isMarkedNullable)
        }

        companion object {
            inline operator fun <reified T> invoke(model: SchemaModel.SchemaModelLitteral<T>): Builder {
                return Builder(
                    getKType<T?>(),
                    model
                )
            }

            inline operator fun <reified T> invoke(type: DataType, format: DataFormat? = null): Builder {
                return Builder(
                    SchemaModel.SchemaModelLitteral<T>(type, format)
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
        Builder<Instant>(
            DataType.string,
            DataFormat.`date-time`
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

