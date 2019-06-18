package com.papsign.ktor.openapigen.exceptions

class OpenAPIBadContentException(msg: String): Exception(msg)

inline fun assertContent(bool: Boolean, crossinline err: () -> String) {
    if (!bool) {
        throw OpenAPIBadContentException(err())
    }
}