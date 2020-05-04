package com.papsign.ktor.openapigen.model.info

import com.papsign.ktor.openapigen.model.DataModel

data class InfoModel(
    var title: String = "Default",
    var version: String = "0.0.1",
    var description: String? = null,
    var termsOfService: String? = null,
    var contact: ContactModel? = null,
    var license: LicenseModel? = null
): DataModel
