package com.papsign.ktor.openapigen.openapi

data class ExternalDocumentation(
    var url: String,
    var description: String? = null
)

data class Parameter<T>(
    var name: String,
    var `in`: ParameterLocation,
    var required: Boolean = true,
    var description: String? = null,
    var deprecated: Boolean? = null,
    var allowEmptyValue: Boolean? = null,
    var schema: Schema<T>? = null,
    var example: T? = null,
    var examples: MutableMap<String, T>? = null
    // incomplete
)

interface Ref<T> {
    val `$ref`: String
}

enum class ParameterLocation {
    query, header, path, cookie
}

enum class DataType {
    integer, number, string, boolean, `object`, array
}

enum class DataFormat {
    int32, int64, float, double, string, byte, binary, date, `date-time`, password, email, uuid
}

data class RequestBody<T>(
    var content: MutableMap<String, MediaType<T>>,
    var description: String? = null,
    var required: Boolean? = null
)

data class MediaType<T>(
    var schema: Schema<T>? = null,
    var example: T? = null,
    var examples: MutableMap<String, T>? = null
)

data class StatusResponse(
    var description: String,
    var headers: MutableMap<String, Ref<Header<*>>> = mutableMapOf(),
    var content: MutableMap<String, MediaType<*>> = mutableMapOf()
    //links
)

data class Header<T>(
    var required: Boolean,
    var description: String? = null,
    var deprecated: Boolean? = null,
    var allowEmptyValue: Boolean? = null,
    var schema: Schema<T>? = null,
    var example: T? = null,
    var examples: MutableMap<String, T>? = null
    // incomplete
)

data class SecurityScheme<T> constructor(
    val type: SecuritySchemeType,
    val name: String,
    val `in`: APIKeyLocation? = null,
    val scheme: HttpSecurityScheme? = null,
    val bearerFormat: String? = null,
    val flows: Flows<T>? = null,
    val openIdConnectUrl: String? = null
) where T : Enum<T>, T : Described

enum class SecuritySchemeType {
    apiKey, http, oauth2, openIdConnect
}

enum class HttpSecurityScheme {
    basic, bearer, digest
}

enum class APIKeyLocation {
    query, header, cookie
}

class Security : MutableMap<String, List<*>> by mutableMapOf() {

    operator fun <T> set(scheme: SecurityScheme<T>, requirements: List<T>) where T: Enum<T>, T: Described {
        this[scheme.name] = requirements
    }

    fun <T> set(scheme: SecurityScheme<T>) where T: kotlin.Enum<T>, T: Described {
        this[scheme] = listOf()
    }
}

data class Components(
    var schemas: MutableMap<String, Schema<*>> = sortedMapOf(),
    var responses: MutableMap<String, StatusResponse> = sortedMapOf(),
    var parameters: MutableMap<String, Parameter<*>> = sortedMapOf(),
    var examples: MutableMap<String, Example<*>> = sortedMapOf(),
    var requestBodies: MutableMap<String, RequestBody<*>> = sortedMapOf(),
    var headers: MutableMap<String, Header<*>> = sortedMapOf(),
    var securitySchemes: MutableMap<String, SecurityScheme<*>> = sortedMapOf()
    //links
    //callbacks
)

data class Example<T>(
    var value: T,
    var summary: String? = null,
    var description: String? = null
)


data class Tag(
    val name: String,
    val description: String? = null,
    val externalDocs: ExternalDocumentation? = null
)


