package com.papsign.ktor.openapigen.annotations.type.string.pattern

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

abstract class RegularExpressionConstraintProcessor<A: Annotation>(): SchemaProcessor<A>, ValidatorBuilder<A> {

    private val log = classLogger()

    val types = listOf(getKType<String>().withNullability(true), getKType<String>().withNullability(false))

    abstract fun process(model: SchemaModel.SchemaModelLitteral<*>, annotation: A): SchemaModel.SchemaModelLitteral<*>

    abstract fun getConstraint(annotation: A): RegularExpressionConstraint

    private class RegularExpressionConstraintValidator(private val constraint: RegularExpressionConstraint): Validator {
        override fun <T> validate(subject: T?): T? {
            if (subject is String?) {
                if (subject == null || !constraint.pattern.toRegex().containsMatchIn(subject)) {
                    throw RegularExpressionConstraintViolation(subject, constraint)
                }
            } else {
                throw NotAStringViolation(subject)
            }
            return subject
        }
    }

    override fun build(type: KType, annotation: A): Validator {
        return if (types.contains(type)) {
            RegularExpressionConstraintValidator(getConstraint(annotation))
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

data class RegularExpressionConstraint(val pattern: String, val errorMessage: String)

class RegularExpressionConstraintViolation(val actual: String?, val constraint: RegularExpressionConstraint): ConstraintViolation("Constraint violation: the string " +
"'$actual' does not match the regular expression ${constraint.pattern}", constraint.errorMessage)