package com.papsign.ktor.openapigen.annotations.parameters

import kotlin.reflect.full.findAnnotation

internal val HeaderParam.apiParam: APIParam
    get() = annotationClass.findAnnotation()!!

internal val QueryParam.apiParam: APIParam
    get() = annotationClass.findAnnotation()!!

internal val PathParam.apiParam: APIParam
    get() = annotationClass.findAnnotation()!!