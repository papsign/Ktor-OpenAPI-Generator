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

object DefaultMapSchemaProvider: SchemaBuilderProviderModule, OpenAPIGenModuleExtension, DefaultOpenAPIModule {

    override fun provide(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<SchemaBuilder> {
        return listOf(Builder)
    }

    private object Builder : SchemaBuilder {
        override val superType: KType = getKType<Map<*, *>?>()
        override fun build(type: KType, builder: FinalSchemaBuilder, finalize: (SchemaModel<*>)->SchemaModel<*>): SchemaModel<*> {
            checkType(type)
            if (type.arguments[0].type != getKType<String>()) error("bad type $type: Only maps with string keys are supported")
            val valueType = type.arguments[1].type ?: error("bad type $type: star projected types are not supported")
            @Suppress("UNCHECKED_CAST")
            return finalize(SchemaModel.SchemaModelMap(
                builder.build(valueType) as SchemaModel<Any?>,
                type.isMarkedNullable
            ))
        }
    }
}

