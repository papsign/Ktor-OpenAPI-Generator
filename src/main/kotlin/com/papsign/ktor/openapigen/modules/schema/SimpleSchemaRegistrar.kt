package com.papsign.ktor.openapigen.modules.schema

import com.papsign.kotlin.reflection.getKType
import com.papsign.kotlin.reflection.toKType
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.openapi.Schema
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

open class SimpleSchemaRegistrar(val namer: SchemaNamer) : SchemaRegistrar {

    private val log = classLogger<SchemaRegistrar>()

    override fun get(type: KType, master: SchemaRegistrar) = NamedSchema(namer[type], master.makeSchema(type))

    private fun SchemaRegistrar.makeSchema(type: KType): Schema<*> {
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

    private fun SchemaRegistrar.makeEnumSchema(type: KType): Schema<*> {
        return Schema.SchemaEnum<Any>(
            type.jvmErasure.java.enumConstants.map { (it as Enum<*>).name },
            type.isMarkedNullable
        )
    }

    private fun SchemaRegistrar.makeListSchema(type: KType): Schema<*> {
        return Schema.SchemaArr<Any>(
            get(type.arguments[0].type!!).schema
        )
    }

    private fun SchemaRegistrar.makeArraySchema(type: KType): Schema<*> {
        return Schema.SchemaArr<Any>(
            get(type.jvmErasure.java.componentType.toKType()).schema
        )
    }

    private fun SchemaRegistrar.makeObjectSchema(type: KType): Schema<*> {

        val props = type.jvmErasure.declaredMemberProperties.filter { it.visibility == KVisibility.PUBLIC }
        val properties = props.associate {
            Pair(it.name, get(it.returnType).schema)
        }
        if (properties.isEmpty()) log.warn("No public properties found in object $type")
        return Schema.SchemaObj<Any>(
            properties,
            props.filter { !it.returnType.isMarkedNullable }.map { it.name })
    }

    private fun SchemaRegistrar.makeMapSchema(type: KType): Schema<*> {
        val type = type.arguments[1].type ?: getKType<String>()
        return Schema.SchemaMap(get(type).schema as Schema<Any>)
    }
}