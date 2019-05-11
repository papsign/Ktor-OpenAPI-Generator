package com.papsign.ktor.openapigen.modules

interface DependentModule {
    val handlers: Collection<OpenAPIModule>
}