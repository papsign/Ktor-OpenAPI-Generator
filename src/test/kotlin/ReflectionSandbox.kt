import org.junit.Test
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.reflect

class ReflectionSandbox {

    object get : EndpointHandler() {
        override fun <R> handle(kfun: KFunction<R>) {
            println(kfun)
            println(kfun.parameters.associateWith { it.type.annotations }.mapKeys { it.key.type })
            println(kfun.returnType)
        }
    }

    @Target(AnnotationTarget.TYPE)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TestParam


    data class BodyResponse<T>(val payload: T)

    @Test
    fun testNoInlineParameterReflection() {
        get { a: @TestParam String ->

        }
    }
}

abstract class EndpointHandler {

    abstract fun <R> handle(kfun: KFunction<R>)

    inline operator fun <reified R> invoke(noinline test: () -> R) {
        handle(test.reflect()!!)
    }

    inline operator fun <reified R, reified A> invoke(noinline test: (a: A) -> R) {
        handle(test.reflect()!!)
    }

    inline operator fun <reified R, reified A, reified B> invoke(noinline test: (a: A, b: B) -> R) {
        handle(test.reflect()!!)
    }

    inline operator fun <reified R, reified A, reified B, reified C> invoke(noinline test: (a: A, b: B, c: C) -> R) {
        handle(test.reflect()!!)
    }

    inline operator fun <reified R, reified A, reified B, reified C, reified D> invoke(noinline test: (a: A, b: B, c: C, d: D) -> R) {
        handle(test.reflect()!!)
    }
}
