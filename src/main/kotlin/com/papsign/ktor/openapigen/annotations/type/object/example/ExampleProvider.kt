package com.papsign.ktor.openapigen.annotations.type.`object`.example

interface ExampleProvider<T> {
    val example: T?
        get() = null
    val examples: List<T>?
        get() = null
}
