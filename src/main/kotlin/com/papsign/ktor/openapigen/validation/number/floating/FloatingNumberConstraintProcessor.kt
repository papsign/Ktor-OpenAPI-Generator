package com.papsign.ktor.openapigen.validation.number.floating

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.validation.number.NumberConstraintProcessor

abstract class FloatingNumberConstraintProcessor<A: Annotation>: NumberConstraintProcessor<A>(listOf(
    getKType<Float>(),
    getKType<Double>()
))
