package com.papsign.ktor.openapigen.exceptions

import kotlin.reflect.KClass

class OpenAPIParseException(val request: KClass<*>, val actual: Set<KClass<*>>): Exception("Could not parse $request as $actual")
