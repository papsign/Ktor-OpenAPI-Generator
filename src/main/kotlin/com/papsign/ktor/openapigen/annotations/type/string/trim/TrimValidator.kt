package com.papsign.ktor.openapigen.annotations.type.string.trim

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.validation.Validator
import com.papsign.ktor.openapigen.annotations.type.SingleTypeValidator

object TrimValidator : SingleTypeValidator<Trim>(getKType<String>(), { TrimValidator }), Validator {
    override fun <T> validate(subject: T?): T? {
        @Suppress("UNCHECKED_CAST")
        return (subject as String?)?.trim() as T?
    }
}
