package com.papsign.ktor.openapigen.parameters.parsers.builders.query.deepobject

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.parsers.builders.Builder

interface DeepBuilder :
    Builder<QueryParamStyle> {
    override val style: QueryParamStyle
        get() = QueryParamStyle.deepObject
    override val explode: Boolean
        get() = true
}

