package com.papsign.ktor.openapigen.model.security

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.Described

data class SecuritySchemeModel<T> constructor(
    val type: SecuritySchemeType,
    val name: String,
    val `in`: APIKeyLocation? = null,
    val scheme: HttpSecurityScheme? = null,
    val bearerFormat: String? = null,
    val flows: FlowsModel<T>? = null,
    val openIdConnectUrl: String? = null
): DataModel where T : Enum<T>, T : Described
