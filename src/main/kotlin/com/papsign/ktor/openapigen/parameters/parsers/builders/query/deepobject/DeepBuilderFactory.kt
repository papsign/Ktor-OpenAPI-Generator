package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.ConverterFormBuilder

object DeepBuilderFactory : BuilderSelectorFactory<Builder<QueryParamStyle>, QueryParamStyle>(
    ConverterFormBuilder.primitive,
    ListDeepBuilder,
    ArrayDeepBuilder,
    MapDeepBuilder,
    ObjectDeepBuilder
)
