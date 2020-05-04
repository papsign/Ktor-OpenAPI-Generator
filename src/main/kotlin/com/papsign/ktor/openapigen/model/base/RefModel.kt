package com.papsign.ktor.openapigen.model.base

import com.papsign.ktor.openapigen.model.DataModel

interface RefModel<T>: DataModel {
    val `$ref`: String
}
