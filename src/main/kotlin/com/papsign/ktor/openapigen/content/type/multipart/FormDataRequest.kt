package com.papsign.ktor.openapigen.content.type.multipart

import com.papsign.ktor.openapigen.annotations.encodings.APIRequestFormat

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@APIRequestFormat
annotation class FormDataRequest(val type: FormDataRequestType = FormDataRequestType.MULTIPART)
