package com.papsign.ktor.openapigen

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.security.HttpSecurityScheme
import com.papsign.ktor.openapigen.model.security.SecurityModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeModel
import com.papsign.ktor.openapigen.model.security.SecuritySchemeType
import com.papsign.ktor.openapigen.modules.providers.AuthProvider
import org.junit.Test


internal class SomeTests {

    @Test
    fun `test security model`() {
        val securities = AuthProvider.Security(
            SecuritySchemeModel(
                SecuritySchemeType.http,
                scheme = HttpSecurityScheme.bearer,
                bearerFormat = "JWT",
                name = "jwtAuth"
            ), emptyList<TestServerWithJwtAuth.Scopes>()
        )
        val securityModel = SecurityModel()
        securityModel.set(securities.scheme, securities.requirements)
        securityModel.get(securities.scheme.name).also { println("Getting ${securities.scheme.name} - $it") }
        Container(securities = listOf(securityModel)).serialize().also { println(it) }
        (securityModel is Map<*, *>).also { println("Is securityModel a Map<*, *>? $it") }
    }
}

data class Container(
    val securities: List<SecurityModel>,
    val securityMap: List<Map<String, List<*>>> = listOf(
        mapOf("jwtAuth" to listOf(TestServerWithJwtAuth.Scopes.Profile, TestServerWithJwtAuth.Scopes.Profile))
    )
) : DataModel