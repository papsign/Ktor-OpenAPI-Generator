package com.papsign.ktor.openapigen.validators

import com.papsign.kotlin.reflection.getKType
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.ofClass
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

class ValidationHandler<T>(type: KType, context: ModuleProvider<*>) {
    private val log = classLogger()

    private val transformFun: ((T) -> T)?

    init {
        val nonNullType = type.withNullability(false)
        when {
            nonNullType.isSubtypeOf(arrayErasure) -> {
                val arrtype = type.arguments[0].type!!
                val handler = ValidationHandler<Any?>(arrtype, context)
                transformFun = if (handler.isUseful()) {
                    { t: T -> if (t != null) (t as Array<Any?>).map { handler.handle(it) }.toTypedArray() as T else t }
                } else null
            }
            nonNullType.isSubtypeOf(iterableErasure) -> {
                val listtype = type.arguments[0].type!!
                val handler = ValidationHandler<Any?>(listtype, context)
                transformFun = when {
                    nonNullType.isSupertypeOf(listErasure) -> {
                        log.error("Supertypes of List are not yet supported, ignoring $type")
                        null
                    }
                    handler.isUseful() -> {
                        { t: T -> if (t != null) (t as Iterable<Any?>).map { handler.handle(it) } as T else t }
                    }
                    else -> null
                }
            }
            else -> {
                val validators = context.ofClass<Validator<Any, Annotation>>()
                val eligibleProperties = try {
                    type.jvmErasure.memberProperties
                } catch (e: Error) {
                    listOf<KProperty1<T, *>>()
                }
                val targets = eligibleProperties.associateWith { prop ->
                    val annotations = prop.annotations.map { it.annotationClass }.toSet()
                    val handler = ValidationHandler<Any?>(prop.returnType, context)
                    val appliedValidators = validators.filter {
                        val applied = (annotations as Set<KClass<Annotation>>).contains(it.annotationClass)
                        if (applied && !it.isTypeSupported(prop.returnType.jvmErasure)) {
                            log.error("Validator ${it::class.simpleName} does not support type ${prop.returnType} of $prop and will be ignored")
                            false
                        } else {
                            applied
                        }
                    }.associateWith {
                        prop.annotations.filter { annot -> it.annotationClass.isInstance(annot) }
                    }
                    val validations = appliedValidators.flatMap { (valid, annots) ->
                        annots.map { annot ->
                            log.trace("Applying validator ${valid::class.simpleName} with @${annot.annotationClass.simpleName} on $prop");
                            { t: Any? -> valid.validate(t, annot) }
                        }
                    }
                    if (validations.isNotEmpty()) {
                        if (handler.isUseful()) {
                            { t: Any? -> validations.fold(handler.handle(t)) { v, op -> op(v) } }
                        } else {
                            { t: Any? -> validations.fold(t) { v, op -> op(v) } }
                        }
                    } else {
                        if (handler.isUseful()) {
                            handler::handle
                        } else {
                            null
                        }
                    }
                }.filterValues { it != null } as Map<KProperty1<T, *>, (Any?) -> Any?>
                transformFun = when {
                    targets.isEmpty() -> null
                    type.jvmErasure.isData -> {
                        val copy = type.jvmErasure.functions.find { it.name == "copy" } as KFunction<T>
                        val propMap = copy.parameters.associateBy { it.name }
                        val mapped = targets.map { (prop, fn) -> Pair(propMap[prop.name]!!, Pair(prop, fn)) }.associate { it }
                        val instanceParam = copy.instanceParameter!!
                        { t: T -> if (t != null) copy.callBy(mapOf(instanceParam to t) + mapped.mapValues { it.value.second(it.value.first.get(t)) }) else t }
                    }
                    else -> {
                        log.error("Validators are only supported on data classes, tried on: $type")
                        null
                    }
                }
            }
        }
    }

    fun handle(t: T): T {
        return if (t != null) transformFun?.invoke(t) ?: t else t
    }

    fun isUseful(): Boolean {
        return transformFun != null
    }

    companion object {
        inline operator fun <reified T : Any> invoke(provider: ModuleProvider<*>): ValidationHandler<T> {
            return ValidationHandler(getKType<T>(), provider)
        }
    }
}

val arrayErasure = getKType<Array<*>>().jvmErasure.starProjectedType
val iterableErasure = getKType<Iterable<*>>().jvmErasure.starProjectedType
val listErasure = getKType<List<*>>().jvmErasure.starProjectedType
