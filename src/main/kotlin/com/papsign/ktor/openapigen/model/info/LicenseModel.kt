package com.papsign.ktor.openapigen.model.info

import com.papsign.ktor.openapigen.model.DataModel

data class LicenseModel(
    var name: String = "All Rights Reserved",
    var url: String? = null
): DataModel
