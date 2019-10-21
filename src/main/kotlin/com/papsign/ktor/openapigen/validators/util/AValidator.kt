package com.papsign.ktor.openapigen.validators.util

import com.papsign.ktor.openapigen.OpenAPIGenModuleExtension
import com.papsign.ktor.openapigen.validators.Validator
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


abstract class AValidator<T : Any, A : Annotation>(val t: KClass<T>, a: KClass<A>) : Validator<T, A>, OpenAPIGenModuleExtension {
    override fun isTypeSupported(clazz: KClass<*>): Boolean {
        return clazz.isSubclassOf(t)
    }
    override val annotationClass: KClass<A> = a
    override val exceptionClasses: List<KClass<*>> = listOf()
}
