package com.papsign.ktor.openapigen.validators

import com.papsign.ktor.openapigen.validators.util.AValidator

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@ValidationAnnotation
annotation class LowerCase

object LowerCaseValidator: AValidator<String, LowerCase>(String::class, LowerCase::class) {
    override fun validate(subject: String?, annotation: LowerCase): String? {
        return subject?.toLowerCase()
    }
}
