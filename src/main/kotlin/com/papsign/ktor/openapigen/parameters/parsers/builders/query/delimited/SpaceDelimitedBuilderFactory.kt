package com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.ConverterFormBuilder

object SpaceDelimitedBuilderFactory: BuilderSelectorFactory<Builder<QueryParamStyle>, QueryParamStyle>(
    ConverterFormBuilder.primitive,
    ArraySpaceDelimitedBuilder,
    ListSpaceDelimitedBuilder,
    ConverterFormBuilder.all
)
