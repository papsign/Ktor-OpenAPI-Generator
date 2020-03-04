package com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderSelectorFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.ConverterFormBuilder

object PipeDelimitedBuilderFactory : BuilderSelectorFactory<Builder<QueryParamStyle>, QueryParamStyle>(
    ConverterFormBuilder.primitive,
    ArrayPipeDelimitedBuilder,
    ListPipeDelimitedBuilder,
    ConverterFormBuilder.all
)
