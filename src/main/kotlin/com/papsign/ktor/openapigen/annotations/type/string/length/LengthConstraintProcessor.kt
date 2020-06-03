package com.papsign.ktor.openapigen.annotations.type.string.length

import com.papsign.ktor.openapigen.annotations.type.common.ConstraintViolation
import com.papsign.ktor.openapigen.annotations.type.string.NotAStringViolation
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.SchemaProcessor
import com.papsign.ktor.openapigen.validation.Validator
import com.papsign.ktor.openapigen.validation.ValidatorBuilder
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

abstract class LengthConstraintProcessor<A: Annotation>(): SchemaProcessor<A>, ValidatorBuilder<A> {

    private val log = classLogger()

    val types = listOf(getKType<String>().withNullability(true), getKType<String>().withNullability(false))

    abstract fun process(model: SchemaModel.SchemaModelLitteral<*>, annotation: A): SchemaModel.SchemaModelLitteral<*>

    abstract fun getConstraint(annotation: A): LengthConstraint

    private class LengthConstraintValidator(private val constraint: LengthConstraint): Validator {
        override fun <T> validate(subject: T?): T? {
            if (subject is String?) {
                val value = subject?.length ?: 0
                if (constraint.min != null) {
                    if (value < constraint.min) throw LengthConstraintViolation(value, constraint)
                }
                if (constraint.max != null) {
                    if (value > constraint.max) throw LengthConstraintViolation(value, constraint)
                }
            } else {
                throw NotAStringViolation(subject)
            }
            return subject
        }
    }

    override fun build(type: KType, annotation: A): Validator {
        return if (types.contains(type)) {
            LengthConstraintValidator(getConstraint(annotation))
        } else {
            error("${annotation::class} can only be used on types: $types")
        }
    }

    override fun process(model: SchemaModel<*>, type: KType, annotation: A): SchemaModel<*> {
        return if (model is SchemaModel.SchemaModelLitteral<*> && types.contains(type)) {
            process(model, annotation)
        } else {
            log.warn("${annotation::class} can only be used on types: $types")
            model
        }
    }
}

data class LengthConstraint(val min: Int? = null, val max: Int? = null, val errorMessage: String)

class LengthConstraintViolation(val actual: Number?, val constraint: LengthConstraint): ConstraintViolation("Constraint violation: the length of the string should be ${
{
    val min = "${constraint.min}"
    val max = "${constraint.max}"
    when {
        constraint.min != null && constraint.max != null -> "between $min and $max"
        constraint.min != null -> "at least $min"
        constraint.max != null -> "at most $max"
        else -> "anything"
    }
}()
}, but it is $actual", constraint.errorMessage)