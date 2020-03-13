package com.papsign.ktor.openapigen.model.info

import com.papsign.ktor.openapigen.model.DataModel

data class ExampleModel<T>(
    var value: T,
    var summary: String? = null,
    var description: String? = null
): DataModel
