package com.papsign.ktor.openapigen.model.operation

import com.papsign.ktor.openapigen.model.DataModel
import com.papsign.ktor.openapigen.model.base.RefModel

data class StatusResponseModel(
    var description: String,
    var headers: MutableMap<String, RefModel<HeaderModel<*>>> = mutableMapOf(),
    var content: MutableMap<String, MediaTypeModel<*>> = mutableMapOf()
    //links
): DataModel
