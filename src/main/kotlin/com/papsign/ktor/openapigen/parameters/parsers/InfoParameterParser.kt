package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.parameters.PathParamStyle
import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.util.ParameterInfo

abstract class InfoParameterParser(
    info: ParameterInfo,
    queryStyle: (QueryParamStyle) -> Pair<QueryParamStyle, Boolean?>,
    pathStyle: (PathParamStyle) -> Pair<PathParamStyle, Boolean?>
) : ParameterParser {

    constructor(info: ParameterInfo, queryStyle: QueryParamStyle = QueryParamStyle.form, pathStyle: PathParamStyle = PathParamStyle.simple) : this(
        info,
        genReplace(QueryParamStyle.DEFAULT, queryStyle),
        genReplace(PathParamStyle.DEFAULT, pathStyle)
    )

    constructor(info: ParameterInfo, queryStyle: (QueryParamStyle) -> Pair<QueryParamStyle, Boolean?>, pathStyle: PathParamStyle = PathParamStyle.simple) : this(
        info,
        queryStyle,
        genReplace(PathParamStyle.DEFAULT, pathStyle)
    )

    constructor(info: ParameterInfo, queryStyle: QueryParamStyle = QueryParamStyle.form, pathStyle: (PathParamStyle) -> Pair<PathParamStyle, Boolean?>) : this(
        info,
        genReplace(QueryParamStyle.DEFAULT, queryStyle),
        pathStyle
    )

    final override val key: String = info.key
    final override val pathStyle: PathParamStyle?
    final override val queryStyle: QueryParamStyle?
    final override val explode: Boolean

    init {
        val (path, explodePath) = info.pathAnnotation?.style?.let(pathStyle) ?: null to null
        val (query, explodeQuery) = info.queryAnnotation?.style?.let(queryStyle) ?: null to null
        this.pathStyle = path
        this.queryStyle = query
        val baseExplode = info.pathAnnotation?.explode ?: info.queryAnnotation!!.explode
        val explodeOverride = explodePath ?: explodeQuery
        if (explodeOverride != null && explodeOverride != baseExplode) log.warn("Overriding explode $baseExplode to $explodeOverride for style ${path ?: query}")
        this.explode = explodeOverride ?: baseExplode
    }

    companion object {

        val log = classLogger<InfoParameterParser>()

        private fun <T> genReplace(default: T, replace: T): (T) -> Pair<T, Boolean?> {
            return { value ->
                when (value) {
                    default -> replace to null
                    else -> value to null
                }
            }
        }
    }
}
