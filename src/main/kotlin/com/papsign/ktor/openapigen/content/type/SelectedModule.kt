package com.papsign.ktor.openapigen.content.type

import com.papsign.ktor.openapigen.modules.OpenAPIModule

interface SelectedModule: OpenAPIModule {
    val module: OpenAPIModule
}