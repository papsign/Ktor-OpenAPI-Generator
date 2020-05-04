package com.papsign.ktor.openapigen.model.base

import com.papsign.ktor.openapigen.model.operation.OperationModel
import com.papsign.ktor.openapigen.model.operation.ParameterModel
import com.papsign.ktor.openapigen.model.server.ServerModel

@Suppress("UNCHECKED_CAST")
class PathItemModel : MutableMap<String, Any> by HashMap() {
    var summary: String? by (this as MutableMap<String, String?>)
    var description: String? by (this as MutableMap<String, String?>)
    var get: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var put: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var post: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var delete: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var options: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var head: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var patch: OperationModel? by (this as MutableMap<String, OperationModel?>)
    var trace: OperationModel? by (this as MutableMap<String, OperationModel?>)
    val servers: MutableList<ServerModel>
        get() = (this as MutableMap<String, MutableList<ServerModel>>).getOrPut("servers") { mutableListOf() }
    var parameters: List<ParameterModel<*>>
        get() = (this as MutableMap<String, List<ParameterModel<*>>>).getOrPut("parameters") { listOf() }
        set(value) {
            (this as MutableMap<String, List<ParameterModel<*>>>)["parameters"] = value
        }
}
