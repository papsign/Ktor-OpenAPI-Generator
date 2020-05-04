package com.papsign.ktor.openapigen.model.security

import com.papsign.ktor.openapigen.model.Described

class SecurityModel : MutableMap<String, List<*>> by mutableMapOf() {

    operator fun <T> set(scheme: SecuritySchemeModel<T>, requirements: List<T>) where T: Enum<T>, T: Described {
        this[scheme.name] = requirements
    }

    fun <T> set(scheme: SecuritySchemeModel<T>) where T: Enum<T>, T: Described {
        this[scheme] = listOf()
    }
}
