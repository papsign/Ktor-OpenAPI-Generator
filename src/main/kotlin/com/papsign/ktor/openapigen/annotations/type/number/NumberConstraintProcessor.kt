package com.papsign.ktor.openapigen.annotations.type.number

import com.papsign.ktor.openapigen.annotations.type.common.ConstraintViolation
import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.SchemaProcessor
import com.papsign.ktor.openapigen.validation.Validator
import com.papsign.ktor.openapigen.validation.ValidatorBuilder
import java.math.BigDecimal
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

abstract class NumberConstraintProcessor<A: Annotation>(allowedTypes: Iterable<KType>): SchemaProcessor<A>, ValidatorBuilder<A> {

    private val log = classLogger()

    val types = allowedTypes.flatMap { listOf(it.withNullability(true), it.withNullability(false)) }

    abstract fun process(modelLitteral: SchemaModel.SchemaModelLitteral<*>, annotation: A): SchemaModel.SchemaModelLitteral<*>


    abstract fun getConstraint(annotation: A): NumberConstraint


    private class NumberConstraintValidator(private val constraint: NumberConstraint): Validator {
        override fun <T> validate(subject: T?): T? {
            if (subject is Number) {
                val value = BigDecimal(subject.toString())
                if (constraint.min != null) {
                    if (constraint.minInclusive && value < constraint.min) throw NumberConstraintViolation(value, constraint)
                    if (!constraint.minInclusive && value <= constraint.min) throw NumberConstraintViolation(value, constraint)
                }
                if (constraint.max != null) {
                    if (constraint.maxInclusive && value > constraint.max) throw NumberConstraintViolation(value, constraint)
                    if (!constraint.maxInclusive && value >= constraint.max) throw NumberConstraintViolation(value, constraint)
                }
            } else {
                throw NotANumberViolationViolation(subject)
            }
            return subject
        }
    }

    override fun build(type: KType, annotation: A): Validator {
        return if (types.contains(type)) {
            NumberConstraintValidator(getConstraint(annotation))
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

data class NumberConstraint(val min: BigDecimal? = null, val max: BigDecimal? = null, val minInclusive: Boolean = true, val maxInclusive: Boolean = true)

class NumberConstraintViolation(val actual: Number?, val constraint: NumberConstraint): ConstraintViolation("Constraint violation: $actual should be ${
{
    val min = "${constraint.min} ${if (constraint.minInclusive) "inclusive" else "exclusive"}"
    val max = "${constraint.max} ${if (constraint.maxInclusive) "inclusive" else "exclusive"}"
    when {
        constraint.min != null && constraint.max != null -> "between $min and $max"
        constraint.min != null -> "at least $min"
        constraint.max != null -> "at most $max"
        else -> "anything"
    }
}()
}")

class NotANumberViolationViolation(val value: Any?): ConstraintViolation("Constraint violation: $value is not a number")
