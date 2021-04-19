package com.papsign.ktor.openapigen.annotations.type.common

import java.lang.Exception

abstract class ConstraintViolation(defaultMessage: String, message: String = "", cause: Throwable? = null)
    : Exception(message.ifEmpty { defaultMessage }, cause)