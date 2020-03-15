package com.papsign.ktor.openapigen.schema.processor.number

import com.papsign.ktor.openapigen.classLogger
import com.papsign.ktor.openapigen.model.schema.SchemaModel
import com.papsign.ktor.openapigen.schema.processor.SchemaProcessor
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability

abstract class NumberConstraintProcessor<A: Annotation>(allowedTypes: Iterable<KType>): SchemaProcessor<A> {

    private val log = classLogger()

    val types = allowedTypes.flatMap { listOf(it.withNullability(true), it.withNullability(false)) }

    abstract fun process(modelLitteral: SchemaModel.SchemaModelLitteral<*>, annotation: A): SchemaModel.SchemaModelLitteral<*>

    override fun process(model: SchemaModel<*>, type: KType, annotation: A): SchemaModel<*> {
        return if (model is SchemaModel.SchemaModelLitteral<*> && types.contains(type)) {
            process(model, annotation)
        } else {
            log.warn("${annotation::class} can only be used on types: $types")
            model
        }
    }
}
