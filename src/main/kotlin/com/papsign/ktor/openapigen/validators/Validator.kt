package com.papsign.ktor.openapigen.validators

import com.papsign.ktor.openapigen.modules.OpenAPIModule
import org.jetbrains.annotations.Contract
import kotlin.reflect.KClass

interface Validator<T: Any, A: Annotation>: OpenAPIModule {
    fun isTypeSupported(clazz: KClass<*>): Boolean
    val annotationClass: KClass<A>
    val exceptionClasses: List<KClass<*>>
    /**
     * [subject] the serialized property
     * [annotation] the annotation instance on the property
     * @return the transformed property or [subject] if unchanged
     */
    fun validate(subject: T?, annotation: A): T?
}
