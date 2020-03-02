package com.papsign.ktor.openapigen.parameters.parsers.deepobject

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.generic.Builder
import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.generic.BuilderSelector
import com.papsign.ktor.openapigen.parameters.parsers.generic.SelectorFactory
import com.papsign.ktor.openapigen.parameters.util.primitiveParsers
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

abstract class DeepBuilder : Builder<QueryParamStyle> {
    override val style: QueryParamStyle = QueryParamStyle.deepObject
    override val exploded: Boolean = true
}

object DeepBuilderFactory : SelectorFactory<DeepBuilder, QueryParamStyle>(
    PrimitiveBuilder,
    EnumBuilder,
    ListBuilder,
    ArrayBuilder,
    MapBuilder,
    ObjectBuilder
)
