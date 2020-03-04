package com.papsign.ktor.openapigen.parameters.parsers.builders.query.form

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory

object FormBuilderFactory: BuilderSelectorFactory<Builder<QueryParamStyle>, QueryParamStyle>(
    ConverterFormBuilder.primitive,
    ArrayExplodedFormBuilder,
    ListExplodedFormBuilder,
    ConverterFormBuilder.all
)
