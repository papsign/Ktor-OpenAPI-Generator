package com.papsign.ktor.openapigen.model.info

import com.papsign.ktor.openapigen.model.DataModel

data class ContactModel(
    var name: String? = null,
    var url: String? = null,
    var email: String? = null
): DataModel
