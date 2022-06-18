package com.papsign.ktor.openapigen.parameters.parsers.converters

import kotlin.reflect.KClass
import kotlin.reflect.KType

open class ConverterSelectorFactory(vararg selectors: ConverterSelector): ConverterFactory {
    private val converterSelectors = selectors.toMutableList()

    override fun buildConverter(type: KType): Converter? {
        return converterSelectors.find { it.canHandle(type) }?.create(type)
    }

    fun <T : ConverterSelector> injectConverterBefore(kclass: KClass<T>, converterSelector: ConverterSelector) {
        converterSelectors.find { kclass.isInstance(it) }
            ?.let {
                val index = converterSelectors.indexOf(it)
                val shiftIndex = if (index <= 0) 0 else index

                var previous = converterSelectors.getOrNull(shiftIndex)
                converterSelectors[index] = converterSelector

                if (shiftIndex == (converterSelectors.size - 1)) {
                    previous?.let { converterSelectors.add(it) }
                } else {
                    for (i in (index + 1) until converterSelectors.size) {
                        previous?.let {
                            val current = converterSelectors[i]
                            converterSelectors[i] = it
                            previous = current
                        }
                    }
                    previous?.let { converterSelectors.add(it) }
                }
            }
            ?: converterSelectors.add(converterSelector)
    }

    fun <T : ConverterSelector> injectConverterAfter(kclass: KClass<T>, converterSelector: ConverterSelector) {
        converterSelectors.find { kclass.isInstance(it) }
            ?.let {
                val index = converterSelectors.indexOf(it)

                if (index == (converterSelectors.size - 1)) {
                    converterSelectors.add(converterSelector)
                } else {
                    var previous = converterSelectors.getOrNull(index + 1)
                    converterSelectors[index + 1] = converterSelector

                    for (i in (index + 2) until converterSelectors.size) {
                        previous?.let {
                            val current = converterSelectors[i]
                            converterSelectors[i] = it
                            previous = current
                        }
                    }
                    previous?.let { converterSelectors.add(it) }
                }
            }
            ?: converterSelectors.add(converterSelector)
    }

    fun <T : ConverterSelector> removeConverter(kclass: KClass<T>) {
        converterSelectors.removeIf { kclass.isInstance(it) }
    }
}
