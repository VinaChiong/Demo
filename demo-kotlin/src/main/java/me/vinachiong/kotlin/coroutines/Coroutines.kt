package me.vinachiong.kotlin.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class Coroutines {}

fun main() {
    GlobalScope.launch {
        delay(1000L)
        say("World!")
    }
//    say1("Hello,") // suspend函数仅可以在协程中 或 suspend函数中被调用
    println("Hello,")
//    Thread.sleep(1500L)
    
    
    // 使用 runBlocking创建的协程
    // 执行的线程会等待runBlocking执行完成，让内部不阻塞的方法，达到阻塞效果
    runBlocking {
        delay(2000L)
    }
}

suspend fun say(text: String) {
    println(text)
}