package com.papsign.ktor.openapigen.schema.builder.provider

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.model.schema.DataType
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.schema.builder.FinalSchemaBuilder
import com.papsign.ktor.openapigen.schema.builder.SchemaBuilder
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmErasure

object NothingSchemaProvider: SchemaBuilderProviderModule, OpenAPIGenModuleExtension {

    private object NothingNullableProvider {
        private val value: Nothing? = null
        val type: KType = this::value.returnType
    }

    private object Builder: SchemaBuilder {
        // Currently we can't do it in a more concise way because of https://youtrack.jetbrains.com/issue/KT-37848
        override val superType: KType = NothingNullableProvider.type

        override fun build(
            type: KType,
            builder: FinalSchemaBuilder,
            finalize: (SchemaModel<*>) -> SchemaModel<*>
        ): SchemaModel<*> {
            checkType(type)
            return finalize(SchemaModel.SchemaModelLitteral<Any?>(nullable = true))
        }
    }

    override fun provide(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<SchemaBuilder> {
        return listOf(Builder)
    }
}
