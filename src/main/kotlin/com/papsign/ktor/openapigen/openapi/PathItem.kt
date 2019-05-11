package com.papsign.ktor.openapigen.openapi

@Suppress("UNCHECKED_CAST")
class PathItem : MutableMap<String, Any> by HashMap() {
    var summary: String? by (this as MutableMap<String, String?>)
    var description: String? by (this as MutableMap<String, String?>)
    var get: Operation? by (this as MutableMap<String, Operation?>)
    var put: Operation? by (this as MutableMap<String, Operation?>)
    var post: Operation? by (this as MutableMap<String, Operation?>)
    var delete: Operation? by (this as MutableMap<String, Operation?>)
    var options: Operation? by (this as MutableMap<String, Operation?>)
    var head: Operation? by (this as MutableMap<String, Operation?>)
    var patch: Operation? by (this as MutableMap<String, Operation?>)
    var trace: Operation? by (this as MutableMap<String, Operation?>)
    val servers: MutableList<Server>
        get() = (this as MutableMap<String, MutableList<Server>>).getOrPut("servers") { mutableListOf() }
    var parameters: List<Parameter<*>>
        get() = (this as MutableMap<String, List<Parameter<*>>>).getOrPut("parameters") { listOf() }
        set(value) {
            (this as MutableMap<String, List<Parameter<*>>>)["parameters"] = value
        }
}