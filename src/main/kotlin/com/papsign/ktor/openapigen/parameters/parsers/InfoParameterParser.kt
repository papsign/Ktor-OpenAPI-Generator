package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo

abstract class InfoParameterParser(
    info: ParameterInfo,
    queryStyle: (QueryParamStyle) -> QueryParamStyle,
    pathStyle: (PathParamStyle) -> PathParamStyle
) : ParameterParser {

    constructor(info: ParameterInfo, queryStyle: QueryParamStyle = QueryParamStyle.form, pathStyle: PathParamStyle = PathParamStyle.simple) : this(
        info,
        genReplace(QueryParamStyle.DEFAULT, queryStyle),
        genReplace(PathParamStyle.DEFAULT, pathStyle)
    )

    constructor(info: ParameterInfo, queryStyle: (QueryParamStyle) -> QueryParamStyle, pathStyle: PathParamStyle = PathParamStyle.simple) : this(
        info,
        queryStyle,
        genReplace(PathParamStyle.DEFAULT, pathStyle)
    )

    constructor(info: ParameterInfo, queryStyle: QueryParamStyle = QueryParamStyle.form, pathStyle: (PathParamStyle) -> PathParamStyle) : this(
        info,
        genReplace(QueryParamStyle.DEFAULT, queryStyle),
        pathStyle
    )

    override val key: String = info.key
    override val pathStyle: PathParamStyle? = info.pathAnnotation?.style?.let(pathStyle)
    override val queryStyle: QueryParamStyle? = info.queryAnnotation?.style?.let(queryStyle)
    override val explode: Boolean = info.pathAnnotation?.explode ?: info.queryAnnotation!!.explode

    companion object {
        private fun <T> genReplace(default: T, replace: T): (T) -> T {
            return { value ->
                when (value) {
                    default -> replace
                    else -> value
                }
            }
        }
    }
}
