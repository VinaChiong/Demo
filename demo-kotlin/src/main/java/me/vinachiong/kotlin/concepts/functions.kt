package me.vinachiong.kotlin.concepts

class Functions {
    
    fun multiParams(a: Int, b: Int, c: Int, d: Int) {
    
    }
    
    fun method(vararg list: String = arrayOf()) {
        multiParams(b = 1, d = 2, a=2, c=2)
    }
    
    
    fun functionScope() {
        val visited = mutableListOf<Int>()
        
        val local = fun() {
            visited.add(10)
        }
    }
    
}

