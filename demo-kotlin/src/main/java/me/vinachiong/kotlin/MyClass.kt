package me.vinachiong.kotlin


class MyClass {
    val name = "name"
    
    open class Nested {
        fun haha() {
        
        }
    }
    
    open inner class InnerClass {
        fun haha() {
            println(name)
        }
    }
}


class Example(val cl: MyClass) {
    var num: Int = 100
    
    
    fun printFunctionType() { println("Class method $num") }
    companion object
}

fun Example.printFunctionType(i: Int) { println("Extension function ${i}") }
fun Example.Companion.nilaomuhi(): String {return ""}

var Example.wahaha: String
    get() = ""
    set(value) { this.wahaha = value}


fun main(a: Array<String>) {
    val cl = MyClass()
    
    val e = Example(cl)
    e.printFunctionType()
    e.printFunctionType(1)
    val helloWorld = object {
        val hello = "Hello"
        val world = "World"
        // object expressions extend Any, so `override` is required on `toString()`
        override fun toString() = "$hello $world"
        
        fun haha(){
            println("dfsafsa")
        }
    }
    helloWorld.haha()
    
}



