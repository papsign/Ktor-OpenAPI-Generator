package com.papsign.ktor.openapigen.schema.processor.number.integer

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.schema.processor.number.NumberConstraintProcessor

abstract class IntegerNumberConstraintProcessor<A: Annotation>: NumberConstraintProcessor<A>(listOf(
    getKType<Int>(),
    getKType<Long>(),
    getKType<Float>(),
    getKType<Double>()
))
