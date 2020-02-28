package com.papsign.ktor.openapigen.parameters.parsers

import com.papsign.ktor.openapigen.parameters.QueryParamStyle
import com.papsign.ktor.openapigen.parameters.util.*
import io.ktor.http.Parameters
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

class ObjectParameterParser(info: ParameterInfo, type: KType) : InfoParameterParser(info, {
    when (it) {
        QueryParamStyle.DEFAULT, QueryParamStyle.deepObject -> QueryParamStyle.deepObject
        QueryParamStyle.form -> error("Due to unmanageable ambiguities the form style is forbidden for objects")
        else -> error("Query param style $it is undefined for objects in the OpenAPI Spec")
    }
}) {

    private val builder: (Map<String, Any?>) -> Any?
    private val cvt: (Parameters) -> Any?

    init {
        val kclass = type.jvmErasure
        if (kclass.isData) {
            val constructor = kclass.primaryConstructor ?: error("Parameter objects must have primary constructors")
            val parameters = constructor.parameters
            val parameterMap = parameters.associateBy { it.name!! }
            builder = { map -> constructor.callBy(map.mapKeys { parameterMap[it.key] }.filterKeys { it != null }.mapKeys { it.key!! }) }
            cvt = when (queryStyle) {
                QueryParamStyle.deepObject -> {
                    val builder = getBuilderForType(type)
                    ({ builder.build(key, it) })
                }
                null -> error("Only query params can hold objects")
                else -> error("Query param style $queryStyle is not supported")
            }
        } else {
            error("Only data classes are currently supported as parameter objects")
        }
    }


    override fun parse(parameters: Parameters): Any? {
        return cvt(parameters)
    }

    companion object {

        interface DeepBuilder {
            fun build(path: String, parameters: Parameters): Any?
        }

        class ObjectBuilder(val type: KType) : DeepBuilder {
            private val builderMap: Map<KParameter, DeepBuilder>
            private val constructor: KFunction<Any>
            init {
                val kclass = type.jvmErasure
                if (kclass.isData) {
                    constructor = kclass.primaryConstructor ?: error("Parameter objects must have primary constructors")
                    val parameters = constructor.parameters
                    builderMap = parameters.associate { parameter ->
                        parameter to getBuilderForType(parameter.type)
                    }
                } else {
                    error("Only data classes are currently supported as parameter objects")
                }
            }

            override fun build(path: String, parameters: Parameters): Any? {
                return constructor.callBy(builderMap.mapValues { it.value.build("$path[${it.key.name}]", parameters) })
            }
        }

        class PrimitiveBuilder(val cvt: (String?)->Any?): DeepBuilder {
            override fun build(path: String, parameters: Parameters): Any? {
                return cvt(parameters[path])
            }
        }

        class EnumBuilder(val enumMap: Map<String, Any?>): DeepBuilder {
            override fun build(path: String, parameters: Parameters): Any? {
                return parameters[path]?.let { enumMap[it] }
            }
        }

        class ListBuilder(val type: KType): DeepBuilder {
            private val contentType = type.arguments[0].type!!
            private val builder = getBuilderForType(contentType)
            override fun build(path: String, parameters: Parameters): Any? {
                val names = parameters.names().filter { it.startsWith(path) }.toSet()
                val indices = names.map { it.substring(path.length + 1, it.indexOf("]", path.length)).toInt() }.toSet()
                return indices.fold(ArrayList<Any?>().also { it.ensureCapacity(indices.max()?:0) }) { acc, idx ->
                    acc[idx] = builder.build("$path[$idx]", parameters)
                    acc
                }
            }
        }

        private fun getBuilderForType(type: KType): DeepBuilder {
            primitiveParsers[type]?.let { return PrimitiveBuilder(it) }
            val clazz = type.jvmErasure
            val jclazz = clazz.java
            return when {
                jclazz.isEnum -> EnumBuilder(jclazz.enumConstants.associateBy { it.toString() })
                clazz.isSubclassOf(List::class) -> ListBuilder(type)
                jclazz.isArray -> error("Nested array are not yet supported")
                clazz.isSubclassOf(Map::class) -> error("Nested maps are not yet supported")
                else -> ObjectBuilder(type)
            }
        }
    }
}
