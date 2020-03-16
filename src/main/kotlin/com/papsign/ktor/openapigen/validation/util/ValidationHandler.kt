package com.papsign.ktor.openapigen.validation.util

import com.papsign.ktor.openapigen.*
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.validation.ValidatorAnnotation
import com.papsign.ktor.openapigen.validation.ValidatorBuilder
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.jvmErasure


/**
 * don't mind the evil leak, it's that or a two-step builder structure to be able to handle recursive types
 */
class ValidationHandler private constructor(
    annotatedType: AnnotatedKType,
    leakThis: (ValidationHandler) -> Unit
) {

    private val log = classLogger()

    private val transformFun: ((Any?) -> Any?)?

    private fun ValidatorAnnotation.getHandlerInstance(): ValidatorBuilder<*> {
        return handler.objectInstance ?: error("${ValidatorAnnotation::class.simpleName} handler must be an object")
    }

    init {
        leakThis(this)
        val annotations = annotatedType.annotations
        val type = annotatedType.type
        val validators = annotations.mapNotNull { annot ->
            annot.annotationClass.findAnnotation<ValidatorAnnotation>()
                ?.let { (it.getHandlerInstance() as ValidatorBuilder<Annotation>).build(type, annot) }
        }
        val shouldTransform = validators.isNotEmpty()
        val transform: (Any?) -> Any? = { source: Any? ->
            validators.fold(source) { value, validator -> validator.validate(value) }
        }
        when {
            type.isSubtypeOf(arrayType) -> {
                val contentType = type.arguments[0].type!!
                val handler = build(contentType)
                when {
                    handler.isUseful() && shouldTransform -> {
                        transformFun = { t: Any? ->
                            if (t != null) {
                                val size = java.lang.reflect.Array.getLength(t)
                                (0 until size).forEach {
                                    val value = java.lang.reflect.Array.get(t, it)
                                    java.lang.reflect.Array.set(t, it, handler.handle(value))
                                }
                            }
                            transform(t)
                        }
                    }
                    handler.isUseful() -> {
                        transformFun = { t: Any? ->
                            if (t != null) {
                                val size = java.lang.reflect.Array.getLength(t)
                                (0 until size).forEach {
                                    val value = java.lang.reflect.Array.get(t, it)
                                    java.lang.reflect.Array.set(t, it, handler.handle(value))
                                }
                            }
                            t
                        }
                    }
                    shouldTransform -> {
                        transformFun = transform
                    }
                    else -> {
                        transformFun = null
                    }
                }
            }
            type.isSubtypeOf(iterableType) -> {
                val contentType = type.arguments[0].type!!
                val handler = build(contentType)
                if (type.jvmErasure.isInterface) {
                    when {
                        handler.isUseful() && shouldTransform -> when {
                            type.isSubtypeOf(setType) -> {
                                transformFun = { t: Any? ->
                                    transform(
                                        if (t != null) {
                                            (t as Iterable<Any?>).map { handler.handle(it) }.toSet()
                                        } else {
                                            t
                                        }
                                    )
                                }
                            }
                            type.isSubtypeOf(listType) -> {
                                transformFun = { t: Any? ->
                                    transform(if (t != null) {
                                        (t as Iterable<Any?>).map { handler.handle(it) }
                                    } else {
                                        t
                                    })
                                }
                            }
                            else -> error("Iterable interface $type is not supported, please use List or Set")
                        }
                        handler.isUseful() -> when {
                            type.isSubtypeOf(setType) -> {
                                transformFun = { t: Any? ->
                                    if (t != null) {
                                        (t as Iterable<Any?>).map { handler.handle(it) }.toSet()
                                    } else {
                                        t
                                    }

                                }
                            }
                            type.isSubtypeOf(listType) -> {
                                transformFun = { t: Any? ->
                                    if (t != null) {
                                        (t as Iterable<Any?>).map { handler.handle(it) }
                                    } else {
                                        t
                                    }
                                }
                            }
                            else -> error("Iterable interface $type is not supported, please use List or Set")
                        }
                        shouldTransform -> {
                            transformFun = transform
                        }
                        else -> {
                            transformFun = null
                        }
                    }
                } else {
                    val appropriateConstructor = type.jvmErasure.constructors.find {
                        it.parameters.size == 1 && it.parameters[0].type.isSubtypeOf(iterableType)
                    } ?: error("Unsupported Iterable type $type, must have a constructor that takes an iterable");
                    when {
                        handler.isUseful() && shouldTransform -> {
                            transformFun = { t: Any? ->
                                if (t != null) {
                                    appropriateConstructor.call((t as Iterable<Any?>).map { handler.handle(it) })
                                } else {
                                    t
                                }.let(transform)
                            }
                        }
                        handler.isUseful() -> {
                            transformFun = { t: Any? ->
                                if (t != null) {
                                    appropriateConstructor.call((t as Iterable<Any?>).map { handler.handle(it) })
                                } else {
                                    t
                                }
                            }
                        }
                        shouldTransform -> {
                            transformFun = transform
                        }
                        else -> {
                            transformFun = null
                        }
                    }
                }
            }
            type.jvmErasure.isSealed -> {
                val possibleClasses = type.jvmErasure.sealedSubclasses.map { it }
                val handlers = possibleClasses.associateWith {
                    build(it.starProjectedType, annotatedType.typeAnnotation + annotatedType.additionalAnnotations)
                }
                val useful = handlers.values.any { it.isUseful() }
                when {
                    useful && shouldTransform -> {
                        transformFun = { t: Any? ->
                            transform(
                                if (t != null) {
                                    (handlers[t::class] ?: error("No handler for sealed class ${t::class.starProjectedType}, supposed child of $type")).handle(t)
                                } else {
                                    t
                                }
                            )
                        }
                    }
                    useful -> {
                        transformFun = { t: Any? ->
                            if (t != null) {
                                (handlers[t::class] ?: error("No handler for sealed class ${t::class.starProjectedType}, supposed child of $type")).handle(t)
                            } else {
                                t
                            }
                        }
                    }
                    shouldTransform -> {
                        transformFun = transform
                    }
                    else -> {
                        transformFun = null
                    }
                }
            }
            else -> {
                val handled = type.memberProperties.mapNotNull { prop ->
                    val validator = build(prop)
                    if (validator.isUseful()) {
                        prop.source.javaField.also {
                            if (it == null) {
                                log.warn("Field ${prop.source} could not be processed because delegated properties are not supported")
                            }
                        }?.let {
                            validator to it
                        }
                    } else {
                        null
                    }
                }
                when {
                    handled.isNotEmpty() && shouldTransform -> {
                        transformFun = { t: Any? ->
                            if (t != null) {
                                handled.forEach { (handler, field) ->
                                    field.set(t, handler.handle(field.get(t)))
                                }
                            }
                            transform(t)
                        }
                    }
                    handled.isNotEmpty() -> {
                        transformFun = { t: Any? ->
                            if (t != null) {
                                handled.forEach { (handler, field) ->
                                    val accessible = field.isAccessible
                                    field.isAccessible = true
                                    field.set(t, handler.handle(field.get(t)))
                                    field.isAccessible = accessible
                                }
                            }
                            t
                        }
                    }
                    shouldTransform -> {
                        transformFun = transform
                    }
                    else -> {
                        transformFun = null
                    }
                }
            }
        }
    }

    fun <T> handle(t: T): T {
        return if (t != null) transformFun?.invoke(t) as T ?: t else t
    }

    fun isUseful(): Boolean {
        return transformFun != null
    }

    companion object {

        /**
         * needed because a type is equal to another no matter the annotations
         * @param annotations, be careful that it contains everything, the code may fully rely on it
         */
        data class AnnotatedKType(
            val type: KType,
            val additionalAnnotations: List<Annotation> = listOf(),
            val typeAnnotation: List<Annotation> = type.annotations,
            val classAnnotation: List<Annotation> = type.jvmErasure.annotations
        ) {
            val annotations: List<Annotation>
                get() = classAnnotation + typeAnnotation + additionalAnnotations

            companion object {
                inline operator fun <reified T> invoke(annotations: List<Annotation> = listOf()): AnnotatedKType {
                    val type = getKType<T>()
                    return AnnotatedKType(type, annotations)
                }

                operator fun invoke(prop: KTypeProperty): AnnotatedKType {
                    return AnnotatedKType(
                        prop.type,
                        prop.source.annotations
                    )
                }
            }
        }

        private val map = HashMap<String, ValidationHandler>()

        /**
         * Black Magic: DO NOT TOUCH
         * We use a string because it accounts for the annotations
         */
        fun build(type: AnnotatedKType): ValidationHandler {
            val str = type.toString()
            return map[str] ?: {
                ValidationHandler(type) {
                    map[str] = it
                }
            }()
        }

        inline fun <reified T> build(annotations: List<Annotation> = listOf()): ValidationHandler {
            return build(AnnotatedKType<T>(annotations))
        }

        fun build(prop: KTypeProperty): ValidationHandler {
            return build(AnnotatedKType(prop))
        }

        fun build(type: KType, annotations: List<Annotation> = listOf()): ValidationHandler {
            return build(AnnotatedKType(type, annotations))
        }

        private val arrayType = getKType<Array<*>?>()
        private val iterableType = getKType<Iterable<*>?>()
        private val listType = getKType<List<*>?>()
        private val setType = getKType<Set<*>?>()
        private val mapType = getKType<Map<*, *>?>()
    }
}

