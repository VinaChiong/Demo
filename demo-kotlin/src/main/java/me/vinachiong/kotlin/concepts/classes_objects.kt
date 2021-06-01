package me.vinachiong.kotlin.concepts

open class OpenClass {
    open fun canOverride() { println("open类OpenClass的 sayHello方法，不能重写") }
    fun sayHello() { println("open类OpenClass的 sayHello方法，不能重写") }
}

// 继承OpenClass
class SubClass constructor(val name: String = ""): OpenClass() {
    override fun canOverride() {
        super.canOverride()
        println("子类SubClass 只能重写父类的open函数，sayHello 不行")
    }
}

abstract class AbstractClass {
    abstract fun abstractMethod()
    fun canNotOverride() {
        println("open类AbstractClass的 cantOverride方法，不能重写")
    }
    
    class Impl: AbstractClass() {
        override fun abstractMethod() {
            println("Impl类 实现抽象类 AbstractClass, 不能重写父类的非open函数")
        }
    }
    
    companion object {
        fun toast() {
            println("toast 抽象类 AbstractClass 也有伴生对象")
        }
    }
}

// 接口定义
interface INormal {
    //接口的属性值，子类需要实现
    val toBeImpl1: String
    var toBeImpl2: String
    
    //接口的属性值，子类可重写
    val property: String
        get() = "this is property of Interface INormal"
    
    //接口的函数，子类需要实现
    fun method1(): Int
    
    //接口的函数，子类可重写
    fun default() {
        println("this is default function of Interface INormal")
    }
    
    class Impl: INormal {
        override val toBeImpl1: String
            get() = "INormalImpl toBeImp1"
        override var toBeImpl2: String
            get() = "INormalImpl toBeImp12"
            set(value) {
                println("INormalImpl toBeImp12 可修改")
            }
    
        override fun method1(): Int {
            println("INormalImpl 实现接口 的 method1 方法")
            
            val (a, b) = Pair<Int, Int>(1, 0)
            println("a = $a , b = $b")
            
            return 1
        }
    }
}


sealed class People {
    abstract var aa: Int
    abstract fun eat()
}
