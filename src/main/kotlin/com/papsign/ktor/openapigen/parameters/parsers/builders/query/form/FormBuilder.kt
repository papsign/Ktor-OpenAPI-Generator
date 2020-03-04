package com.papsign.ktor.openapigen.parameters.parsers.builders.query.form

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder

abstract class FormBuilder: Builder<QueryParamStyle> {
    override val style: QueryParamStyle = QueryParamStyle.form
}
