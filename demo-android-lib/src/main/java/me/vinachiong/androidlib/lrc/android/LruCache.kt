package me.vinachiong.androidlib.lrc.android

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class LruCache<K, V> {

    private val map: LinkedHashMap<K, V> = LinkedHashMap(0, 0.75f, true)

    private var size: Int = 0
    private var maxSize: Int

    private var putCount: Int = 0
    private var createCount: Int = 0
    private var evictionCount: Int = 0
    private var hitCount: Int = 0
    private var missCount: Int = 0

    constructor(maxSize: Int) {
        require(maxSize > 0) { "maxSize <= 0" }
        this.maxSize = maxSize
    }

    fun resize(maxSize: Int) {
        require(maxSize > 0) { "maxSize <= 0" }
        synchronized(this) {
            this.maxSize = maxSize
        }
        trimToSize(maxSize)
    }

    operator fun get(key: K?): V? {
        requireNotNull(key) { "key == null" }

        // 线程同步，获取key匹配的值mapValue
        var mapValue: V?
        synchronized(this) {
            mapValue = map[key]
            if (null != mapValue) {
                hitCount++ // 读命中数+1
                return mapValue
            }
            missCount++ // 匹配失败数+1
        }

        // 子类可重写：从本地缓存中创建key对应的value值
        // 如果缓存中不存在，则return null
        val createdValue: V = create(key) ?: return null
        synchronized(this) {
            createCount++ // 创造命中数+1
            mapValue = map.put(key, createdValue) //

            if (mapValue != null) {
                map[key] = mapValue!!
            } else {
                size += safeSizeOf(key, createdValue)
            }
        }

        return if (mapValue != null) {
            //entryRemoved(false, key, craeteValue, mapValue)
            mapValue
        } else {
            trimToSize(maxSize)
            createdValue
        }
    }

//    operator fun put(key: K?, value: V?): V? {
//        requireNotNull(key) { "key == null" }
//        requireNotNull(value) { "value == null" }
//
//        val previous: V?
//        synchronized(this) {
//            putCount++
//            size += sizeOf(key, value)
//            previous = map.put(key, value)
//            if (previous != null) {
//                size -= safeSizeOf(key, previous)
//            }
//        }
//        if (previous != null) {
//            //entryRemoved(false, key, previous, value)
//        }
//        trimToSize(maxSize)
//        return previous
//    }

    fun trimToSize(maxSize: Int) {
        while (true) {
            var key: K?
            var value: V?
            synchronized(this) {
                if (size < 0 || (map.isEmpty() && size != 0)) {
                    throw IllegalStateException("${javaClass.name}.sizeOf() is reporting inconsistent results!")
                }

                if (size <= maxSize) {
                    return@synchronized
                }

                //                val toEvict: Map.Entry<K, V> = map.eldest()
                val toEvict: Map.Entry<K, V> = map.entries.first()

            }
        }
    }

    protected fun create(key: K?): V? = null

    private fun safeSizeOf(key: K, value: V): Int {
        val result = sizeOf(key, value)
        check(result >= 0) { "Negative size: $key=$value" }
        return result
    }

    protected fun sizeOf(key: K, value: V): Int = 1

    fun evictAll() {
        trimToSize(-1)
    }

}