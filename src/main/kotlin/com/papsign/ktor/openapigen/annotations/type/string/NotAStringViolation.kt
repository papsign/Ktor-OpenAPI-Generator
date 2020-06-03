package com.papsign.ktor.openapigen.annotations.type.string

import com.papsign.ktor.openapigen.annotations.type.common.ConstraintViolation

class NotAStringViolation(val value: Any?): ConstraintViolation("Constraint violation: $value is not a string")