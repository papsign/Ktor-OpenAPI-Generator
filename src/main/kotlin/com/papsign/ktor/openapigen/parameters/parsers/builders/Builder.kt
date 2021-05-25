package com.papsign.ktor.openapigen.parameters.parsers.builders

import com.papsign.ktor.openapigen.parameters.ParameterStyle

interface Builder<S> where S: ParameterStyle<S>, S: Enum<S> {
    val style: S
    val explode: Boolean
    fun build(key: String, parameters: BuilderParameters): Any?
}

typealias BuilderParameters = Map<StringWithIgnoreCaseFlag, List<String>>

fun BuilderParameters.withMatchingKey(key: String): List<String>? {
    val actualKey = this.keys.find { it.matches(key) }
    return actualKey?.let { this[it] }
}

class StringWithIgnoreCaseFlag(
    val str: String,
    val ignoreCase: Boolean
)
fun String.toStringWithIgnoreCaseFlag(ignoreCase: Boolean = false): StringWithIgnoreCaseFlag = StringWithIgnoreCaseFlag(this, ignoreCase)

fun StringWithIgnoreCaseFlag.matches(string: String) = str.equals(string, ignoreCase)
fun StringWithIgnoreCaseFlag.startsWithMatching(string: String): Boolean {
    return if (ignoreCase) {
        this.str.toLowerCase().startsWith(string.toLowerCase())
    } else {
        this.str.startsWith(string)
    }
}