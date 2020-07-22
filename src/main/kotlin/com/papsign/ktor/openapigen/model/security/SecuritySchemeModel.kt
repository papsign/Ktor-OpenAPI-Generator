package com.papsign.ktor.openapigen.model.security

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.Described

data class SecuritySchemeModel<TScope> constructor(
    val type: SecuritySchemeType,
    val name: String,
    val `in`: APIKeyLocation? = null,
    val scheme: HttpSecurityScheme? = null,
    val bearerFormat: String? = null,
    val flows: FlowsModel<TScope>? = null,
    val openIdConnectUrl: String? = null
): DataModel where TScope : Enum<TScope>, TScope : Described
