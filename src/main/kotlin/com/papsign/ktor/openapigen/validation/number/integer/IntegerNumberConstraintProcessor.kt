package com.papsign.ktor.openapigen.validation.number.integer

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.validation.number.NumberConstraintProcessor

abstract class IntegerNumberConstraintProcessor<A: Annotation>: NumberConstraintProcessor<A>(listOf(
    getKType<Int>(),
    getKType<Long>(),
    getKType<Float>(),
    getKType<Double>()
))
