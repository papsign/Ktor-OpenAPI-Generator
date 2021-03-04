package com.papsign.ktor.openapigen.model.security

import com.papsign.ktor.openapigen.SerializationSettings
import com.papsign.ktor.openapigen.cleanEmptyValues
import com.papsign.ktor.openapigen.convertToValue
import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.Described

class SecurityModel : MutableMap<String, List<*>> by mutableMapOf(), DataModel {

    operator fun <T> set(scheme: SecuritySchemeModel<T>, requirements: List<T>) where T: Enum<T>, T: Described {
        this[scheme.name] = requirements
    }

    fun <T> set(scheme: SecuritySchemeModel<T>) where T: Enum<T>, T: Described {
        this[scheme] = listOf()
    }

    override fun serialize(): Map<String, Any?> {
        val serializationSettings = SerializationSettings(skipEmptyList = true)
        return this.mapValues { (_, prop) ->
            convertToValue(prop,serializationSettings)
        }.cleanEmptyValues(serializationSettings)
    }
}
