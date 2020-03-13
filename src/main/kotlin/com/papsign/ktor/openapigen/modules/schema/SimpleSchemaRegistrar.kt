package com.papsign.ktor.openapigen.modules.schema

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.jvmErasure

open class SimpleSchemaRegistrar(val namer: SchemaNamer) : SchemaRegistrar {

    private val log = classLogger<SchemaRegistrar>()

    override fun get(type: KType, master: SchemaRegistrar) = NamedSchema(namer[type], master.makeSchema(type))

    private fun SchemaRegistrar.makeSchema(type: KType): SchemaModel<*> {
        val clazz = type.jvmErasure
        val jclazz = clazz.java
        return when {
            jclazz.isEnum -> makeEnumSchema(type)
            clazz.isSubclassOf(List::class) || (jclazz.let { it.isArray && !it.componentType.isPrimitive }) -> {
                makeListSchema(type)
            }
            jclazz.isArray -> makeArraySchema(type)
            clazz.isSubclassOf(Map::class) -> makeMapSchema(type)
            else -> makeObjectSchema(type)
        }
    }

    private fun SchemaRegistrar.makeEnumSchema(type: KType): SchemaModel<*> {
        return SchemaModel.SchemaModelEnum<Any>(
            type.jvmErasure.java.enumConstants.map { (it as Enum<*>).name },
            type.isMarkedNullable
        )
    }

    private fun SchemaRegistrar.makeListSchema(type: KType): SchemaModel<*> {
        return SchemaModel.SchemaModelArr<Any>(
            get(type.arguments[0].type!!).schema
        )
    }

    private fun SchemaRegistrar.makeArraySchema(type: KType): SchemaModel<*> {
        return SchemaModel.SchemaModelArr<Any>(
            get(type.jvmErasure.java.componentType.kotlin.starProjectedType).schema
        )
    }

    private fun SchemaRegistrar.makeObjectSchema(type: KType): SchemaModel<*> {
        val erasure = type.jvmErasure
        val typeParameters = erasure.typeParameters.zip(type.arguments).associate { Pair(it.first.name, it.second.type) }
        if (erasure.isSealed) {
            return SchemaModel.OneSchemaModelOf(erasure.sealedSubclasses.map { get(it.starProjectedType).schema })
        }
        val props = erasure.declaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }.associateWith {
            val retType = it.returnType
            when(val classifier = retType.classifier) {
            is KTypeParameter -> typeParameters[classifier.name] ?: it.returnType
            else -> it.returnType
        } }
        val properties = props.map { (key, value) ->
            Pair(key.name, get(value.withNullability(false)).schema)
        }.associate { it }
        if (properties.isEmpty()) log.warn("No public properties found in object $type")
        return SchemaModel.SchemaModelObj<Any>(
            properties,
            props.filterValues { value -> !value.isMarkedNullable }.map { it.key.name })
    }

    private fun SchemaRegistrar.makeMapSchema(type: KType): SchemaModel<*> {
        val type = type.arguments[1].type ?: getKType<String>()
        return SchemaModel.SchemaModelMap(get(type).schema as SchemaModel<Any>)
    }
}
