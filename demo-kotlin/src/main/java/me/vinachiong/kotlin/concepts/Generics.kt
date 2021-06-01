package me.vinachiong.kotlin.concepts

/**
 * Kotlin泛型：
 * out 声明处协变
 * in  声明处逆变
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class Generics {
    
    open class A {}
    open class B: A() {}
    open class C: B() {}
    open class D: C() {}
    
    interface Source<out X, in Y> {
        fun next(param: Y): X
    }
    
    
    // demo 声明时候，是 泛型实例化
    // X 的范围：[? extends B]
    // Y 的范围：[? super A]
    fun demo(source: Source<B, C>) {
        // 使用 out 声明 X 后，X是协变的
        // 这里 泛型实例化后的 Source<D, D>，等同于 Java的 Source<? extend A, D>
        val ad: Source<A, C> = source as Source<A, C>
        val cd: Source<B, D> = source

        // val dc: Source<D, C> = source as Source<D, C>

        val dd: Source<D, D> = source as Source<D, D>

        // 这里不允许，因为第2个类型参数「Y没有out声明」,
        val db: Source<D, B> = source as Source<D, B>
        val aa: Source<A, A> = source as Source<D, A>
    }
    
    fun demo2() {
        val aa = object: Source<C, A>{
            override fun next(param: A): C {
                return C()
            }
        }
        
        
        
        demo(aa)
    }
}

fun main() {
    val generics = Generics()
    
    // 这里 Sourced 第2个类型参数 Y 声明了in可逆变:
    // fun demo(source: Source<D, C>)能  接受 Generics.Source<Generics.D, Generics.A>
    generics.demo(object: Generics.Source<Generics.D, Generics.A> {
        override fun next(param: Generics.A): Generics.D {
            return Generics.D()
        }
    })
    
    
}