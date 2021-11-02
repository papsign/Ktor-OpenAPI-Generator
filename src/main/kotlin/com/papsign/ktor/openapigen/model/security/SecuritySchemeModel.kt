package com.papsign.ktor.openapigen.model.security

import com.papsign.ktor.openapigen.cleanEmptyValues
import com.papsign.ktor.openapigen.convertToValue
import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.Described
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

data class SecuritySchemeModel<TScope> constructor(
    val type: SecuritySchemeType,
    val referenceName: String,
    val name: String? = null,
    val `in`: APIKeyLocation? = null,
    val scheme: HttpSecurityScheme? = null,
    val bearerFormat: String? = null,
    val flows: FlowsModel<TScope>? = null,
    val openIdConnectUrl: String? = null
): DataModel where TScope : Enum<TScope>, TScope : Described{

    override fun serialize(): Map<String, Any?> {
        return this::class.memberProperties.associateBy { it.name }.mapValues<String, KProperty1<out SecuritySchemeModel<TScope>, Any?>, Any?> { (_, prop) ->
            convertToValue((prop as KProperty1<DataModel, *>).get(this))
        }.filter { it.key != "referenceName" }.cleanEmptyValues()
    }
}
