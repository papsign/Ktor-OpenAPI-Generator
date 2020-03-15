package com.papsign.ktor.openapigen.schema.builder.provider

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import com.papsign.ktor.openapigen.schema.builder.SchemaBuilder
import com.papsign.ktor.openapigen.schema.namer.DefaultSchemaNamer
import com.papsign.ktor.openapigen.schema.namer.SchemaNamer
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

object DefaultObjectSchemaProvider : SchemaBuilderProviderModule, OpenAPIGenModuleExtension {
    private val log = classLogger()

    override fun provide(apiGen: OpenAPIGen, provider: ModuleProvider<*>): List<SchemaBuilder> {
        val namer = provider.ofClass<SchemaNamer>().let {
            val last = it.lastOrNull() ?: DefaultSchemaNamer.also { log.debug("No ${SchemaNamer::class} provided, using ${it::class}") }
            if (it.size > 1) log.warn("Multiple ${SchemaNamer::class} provided, choosing last: ${last::class}")
            last
        }
        return listOf(Builder(apiGen, namer))
    }

    private class Builder(private val apiGen: OpenAPIGen, private val namer: SchemaNamer) : SchemaBuilder {

        override val superType: KType = getKType<Any?>()

        private val refs = HashMap<KType, SchemaModel.SchemaModelRef<*>>()

        override fun build(type: KType, builder: SchemaBuilder): SchemaModel<*> {
            checkType(type)
            val nonNullType = type.withNullability(false)
            return refs[nonNullType] ?: {
                val erasure = nonNullType.jvmErasure
                val name = namer[nonNullType]
                val ref = SchemaModel.SchemaModelRef<Any?>("#/components/schemas/$name")
                refs[nonNullType] = ref // needed to prevent infinite recursion
                val existing = apiGen.api.components.schemas[name]
                val new = if (erasure.isSealed) {
                    SchemaModel.OneSchemaModelOf(erasure.sealedSubclasses.map { builder.build(it.starProjectedType, builder) })
                } else {
                    val typeParameters = erasure.typeParameters.zip(type.arguments).associate { Pair(it.first.name, it.second.type) }
                    val memberMap = erasure.declaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }.associateWith {
                        val retType = it.returnType
                        when(val classifier = retType.classifier) {
                            is KTypeParameter -> typeParameters[classifier.name] ?: it.returnType
                            else -> it.returnType
                        }
                    }.mapKeys { (key, _) -> key.name }
                    val required = memberMap.entries.filter { !it.value.isMarkedNullable }.map { it.key }
                    val memberModels = memberMap.mapValues { (_, value) -> builder.build(value, builder) }
                    SchemaModel.SchemaModelObj<Any?>(memberModels, required)
                }
                if (existing != null && existing != new) log.error("Schema with name $name already exists, and is not the same as the new one, replacing...")
                apiGen.api.components.schemas[name] = new
                ref
            }()
        }
    }
}
