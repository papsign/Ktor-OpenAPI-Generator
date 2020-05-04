package com.papsign.ktor.openapigen.annotations.type.number.floating

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.annotations.type.number.NumberConstraintProcessor

abstract class FloatingNumberConstraintProcessor<A: Annotation>: NumberConstraintProcessor<A>(listOf(
    getKType<Float>(),
    getKType<Double>()
))
