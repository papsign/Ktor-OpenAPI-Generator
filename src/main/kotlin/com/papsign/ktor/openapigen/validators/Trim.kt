package com.papsign.ktor.openapigen.validators

import com.papsign.ktor.openapigen.validators.util.AValidator

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@ValidationAnnotation
annotation class Trim

object TrimValidator : AValidator<String, Trim>(String::class, Trim::class) {
    override fun validate(subject: String?, annotation: Trim): String? {
        return subject?.trim()
    }
}
