package com.papsign.ktor.openapigen.validation

interface Validator {
    /**
     * [subject] the serialized property
     * [annotation] the annotation instance on the property
     * @return the transformed property or [subject] if unchanged
     */
    fun <T> validate(subject: T?): T?
}
