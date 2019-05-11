package com.papsign.ktor.openapigen.route.modules

import com.papsign.ktor.openapigen.modules.providers.MethodProvider
import io.ktor.http.HttpMethod

class HttpMethodProviderModule(override val method: HttpMethod): MethodProvider