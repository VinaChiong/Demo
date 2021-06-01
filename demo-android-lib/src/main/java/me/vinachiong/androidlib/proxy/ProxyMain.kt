package me.vinachiong.androidlib.proxy

import java.lang.reflect.Proxy

/**
 * - Java动态代理
 * - 依赖注入 DI（Dependency Injection）
 *      是面向对象6大设计原则中，IoC（Inversion of Control，控制反转）原则的其中一种实现方式
 *      运行时，遍历注入中的注解
 *   IoC解析：
 *      举例子：
 *          程序在对象Main，要获取对象A来干活，而对象A的获取要通过其他对象B,C,D
 *          直观地，在Main中要分别获取  B, C,D，才能获取到A，也就额外依赖了B,C,D
 *          额外创建一个容器，维护 B+C+D -> A 的逻辑
 *          Main只需要在构造方法中接收A
 *
 * @author vina.chiong
 * @version v1.0.0
 */
object ProxyMain {
    
    fun exampleOfIoc() {
        // 程序 主动创建 Req
        val noneIoc: (String)-> Res =  { url ->
            val req = Req(url)
            Res("Result for: ${req.url}")
        }
    
        val ioc: Req.()-> Res =  {
            Res("Result for: ${this.url}")
        }
    }
    
    data class Req(val url: String)
    data class Res(val result: String)
    
    
    interface HelloWorld { fun sayHello() }
    
    // 接口实现
    class Hello: HelloWorld {
        override fun sayHello() {
            println("Hello World")
        }
    }
    
    fun exampleOfDynamicProxy() {
        // ProxyGenerator
        val helloWorld = Proxy.newProxyInstance(javaClass.classLoader, arrayOf(HelloWorld::class.java)) { proxy, method, args ->
            method.invoke(Hello(), args)
        } as HelloWorld
        helloWorld.sayHello()
    }
    
}