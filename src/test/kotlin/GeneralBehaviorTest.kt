import com.papsign.ktor.openapigen.getKType
import org.junit.Test
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf

class GeneralBehaviorTest {

    @Target(AnnotationTarget.TYPE)
    annotation class TypeAnnotation

    open class Base

    open class A : Base()
    open class B : Base()
    open class BA : B()
    open class BB : B()
    open class BBA : BB()

    @Test
    fun testSubtypeHierarchySet() {
        val set = TreeSet<KType>(kotlin.Comparator { a, b ->
            when {
                a.isSubtypeOf(b) -> -1
                b.isSubtypeOf(a) -> 1
                a == b -> 0
                else -> 1
            }
        })
        set.add(getKType<B>())
        println(set)
        set.add(getKType<BBA>())
        println(set)
        set.add(getKType<A>())
        println(set)
        set.add(getKType<BA>())
        println(set)
        set.add(getKType<BB>())
        println(set)
        set.add(getKType<Base>())
        println(set)
    }

    @Test
    fun testArraySubtypes() {
        assert(!getKType<FloatArray>().isSubtypeOf(getKType<Array<Any>>()))
        assert(!getKType<Array<Any>>().isSubtypeOf(getKType<Iterable<Any>>()))
        assert(getKType<List<Any>>().isSubtypeOf(getKType<Iterable<Any>>()))
        assert(getKType<List<Any>>().isSubtypeOf(getKType<Iterable<Any?>>()))
    }

    @Test
    fun testMapSubtypes() {
        assert(!getKType<Map<String, String>>().isSubtypeOf(getKType<Iterable<Any>>()))
    }

    enum class TestEnum {
        A, B, C
    }
    @Test
    fun testEnumSubtypes() {
        assert(getKType<TestEnum>().isSubtypeOf(getKType<Enum<*>>()))
    }


    lateinit var nothing: Nothing
    val nullableNothing: Nothing? = null

    val nothingType =  ::nothing.returnType
    val nullNothingType =  ::nullableNothing.returnType

    @Test
    fun testNothingSubtypes() {
        println(nothingType)
        println(nullNothingType)
        assert(nothingType.isSubtypeOf(nullNothingType))
        assert(!nullNothingType.isSubtypeOf(nothingType))
    }
}
