package com.papsign.ktor.openapigen.schema.builder.provider

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.DefaultOpenAPIModule
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.schema.builder.FinalSchemaBuilder
import com.papsign.ktor.openapigen.schema.builder.SchemaBuilder
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

object DefaultCollectionSchemaProvider: SchemaBuilderProviderModule, OpenAPIGenModuleExtension, DefaultOpenAPIModule {

    private val builders = mapOf(
        getKType<BooleanArray>() to { _: KType -> getKType<Boolean>() },
        getKType<IntArray>() to { _: KType -> getKType<Int>() },
        getKType<LongArray>() to { _: KType -> getKType<Long>() },
        getKType<FloatArray>() to { _: KType -> getKType<Float>() },
        getKType<DoubleArray>() to { _: KType -> getKType<Double>() },
        getKType<Array<*>>() to { type: KType ->
            type.arguments[0].type ?: error("bad type $type: star projected types are not supported")
        },
        getKType<Iterable<*>>() to { type: KType ->
            type.arguments[0].type ?: error("bad type $type: star projected types are not supported")
        }
    ).mapKeys { (key, _) ->
        key.withNullability(true)
    }.map { (key, value) ->
        Builder(
            key,
            value
        )
    }

    override fun provide(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<SchemaBuilder> {
        return builders
    }

    private data class Builder(override val superType: KType, private val getter: (KType) -> KType) :
        SchemaBuilder {
        override fun build(type: KType, builder: FinalSchemaBuilder): SchemaModel<*> {
            checkType(type)
            return SchemaModel.SchemaModelArr<Any?>(builder.build(getter(type)), type.isMarkedNullable)
        }
    }
}

