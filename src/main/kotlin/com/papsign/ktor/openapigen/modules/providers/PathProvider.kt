package com.papsign.ktor.openapigen.modules.providers

import com.papsign.ktor.openapigen.modules.OpenAPIModule

interface PathProvider : OpenAPIModule {
    val path: String
}