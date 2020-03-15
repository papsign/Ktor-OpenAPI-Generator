package com.papsign.ktor.openapigen.schema.processor.number.floating

import com.papsign.ktor.openapigen.getKType
import com.papsign.ktor.openapigen.schema.processor.number.NumberConstraintProcessor

abstract class FloatingNumberConstraintProcessor<A: Annotation>: NumberConstraintProcessor<A>(listOf(
    getKType<Float>(),
    getKType<Double>()
))
