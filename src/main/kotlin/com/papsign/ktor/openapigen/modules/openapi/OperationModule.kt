package com.papsign.ktor.openapigen.modules.openapi

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.modules.ModuleProvider
import com.papsign.ktor.openapigen.modules.OpenAPIModule

interface OperationModule: OpenAPIModule {
    fun configure(apiGen: OpenAPIGen, provider: ModuleProvider<*>, operation: OperationModel)
}
