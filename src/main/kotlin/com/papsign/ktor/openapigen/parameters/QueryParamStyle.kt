package com.papsign.ktor.openapigen.parameters

import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder
import com.papsign.ktor.openapigen.parameters.parsers.builders.BuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject.DeepBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited.PipeDelimitedBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.delimited.SpaceDelimitedBuilderFactory
import com.papsign.ktor.openapigen.parameters.parsers.builders.query.form.FormBuilderFactory

enum class QueryParamStyle(override val factory: BuilderFactory<Builder<QueryParamStyle>, QueryParamStyle>): ParameterStyle<QueryParamStyle> {
    form(FormBuilderFactory), spaceDelimited(SpaceDelimitedBuilderFactory), pipeDelimited(PipeDelimitedBuilderFactory), deepObject(DeepBuilderFactory)
}
