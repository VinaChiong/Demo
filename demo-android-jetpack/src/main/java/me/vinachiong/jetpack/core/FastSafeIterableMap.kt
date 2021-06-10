package me.vinachiong.jetpack.core

/**
 * SafeIterableMap子类，
 * 通过创建HashMap，通过Key快速查找Value
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class FastSafeIterableMap<K, V> : SafeIterableMap<K, V>(){
    
    private val mHashMap = HashMap<K, Entry<K, V>?>()
    
    /**
     * 重写get方法，直接通过hashMap获取
     *
     */
    override fun get(key: K): Entry<K, V>? {
        return mHashMap[key]
    }
    
    /**
     * 重写putIfAbsent方法，加上对mHashMap的写入
     */
    override fun putIfAbsent(key: K, value: V): V? {
        val entry = mHashMap[key]
        if (null != entry) {
            return entry.value
        }
    
        mHashMap[key] = put(key, value)
        return null
    }
    
    /**
     * 重写remove方法，加上在mHashMap中移除
     */
    override fun remove(key: K): V? {
        val removed = super.remove(key)
        mHashMap.remove(key)
        return removed
    }
    
    fun contains(key: K):Boolean = mHashMap.containsKey(key)
    
    /**
     * 返回指定Key值Entry的前一个节点
     */
    fun ceil(key: K): Map.Entry<K, V>? {
        if (contains(key)) {
            return mHashMap[key]?.mPrevious
        }
        return null
    }
    
}