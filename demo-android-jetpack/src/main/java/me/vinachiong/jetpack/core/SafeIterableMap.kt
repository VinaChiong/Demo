package me.vinachiong.jetpack.core

import java.util.*


/**
 * 仿写 [androidx.arch.core.internal.SafeIterableMap]
 * 特性：
 * - 以链表结构存储数据Entry，但提供Map存取的API，put 和 get
 *
 * @author vina.chiong
 * @version v1.0.0
 */
open class SafeIterableMap<K, V> : Iterable<Map.Entry<K, V>> {
    
    // 双端链表
    internal var mStart: Entry<K, V>? = null
    private var mEnd: Entry<K, V>? = null
    
    // 缓存正在被使用的迭代器
    private val mIterators = WeakHashMap<SupportRemove<K, V>, Boolean>()
    
    // 链表长度
    private var mSize = 0
    
    // 读取
    internal open operator fun get(key: K): Entry<K, V>? {
        var currentNode = mStart
        while (currentNode != null) {
            if (currentNode.mNext == key) {
                break
            }
            currentNode = currentNode.mNext
        }
        return currentNode
    }
    
    open fun putIfAbsent(key: K, value: V): V? {
        val entry = this[key]
        if (null != entry) {
            return entry.value
        }
        put(key, value)
        return null
    }
    
    // 插入链表
    internal open fun put(key: K, value: V): Entry<K, V> {
        val entry = Entry(key, value)
        mSize++
        if (mEnd == null) {
            mStart = entry
            mEnd = mStart
            return entry
        }
        
        mEnd!!.mNext = entry
        entry.mPrevious = mEnd
        mEnd = entry
        return entry
    }
    
    // 从链表中移除，处理好 前后节点接驳
    // 并通知迭代器进行处理
    open fun remove(key: K): V? {
        val entry = this[key]?: return null
        mSize-- // 以防在被迭代遍历时候，进行remove操作
        if (!mIterators.isEmpty()) {
            mIterators.forEach { (iterator, bool) ->
                iterator.supportRemove(entry)
            }
        }
        
        // 从链表中移除，处理好前节点 接驳 到后一个接口
        if (entry.mPrevious != null) {
            entry.mPrevious = entry.mNext
        } else {
            mStart = entry.mNext
        }
        
        // 从链表中移除，处理好后节点 接驳 到前一个接口
        if (entry.mNext != null) {
            entry.mNext!!.mPrevious = entry.mPrevious
        } else {
            mEnd = entry.mPrevious
        } //解除节点的前后关联
        entry.mNext = null
        entry.mPrevious = null
        return entry.value
    }
    
    fun size(): Int = mSize
    
    
    override fun iterator(): Iterator<Map.Entry<K, V>> {
        val iterator = AscendingIterator(mStart, mEnd)
        mIterators.put(iterator, false)
        return iterator
    }
    
    fun descendingIterator(): Iterator<Map.Entry<K, V>?>? {
        return DescendingIterator(mStart, mEnd).apply {
            mIterators.put(this, false)
        }
    }
    
    fun iteratorWithAdditions(): IteratorWithAdditions {
        return IteratorWithAdditions()
    }
    
    fun eldest(): Map.Entry<K, V?>? = mStart // 最早加入的记录
    fun newest(): Map.Entry<K, V?>? = mEnd  // 最新加入的记录
    
    override fun equals(other: Any?): Boolean {
        if (other == this) return true
        
        if (other != null && other !is SafeIterableMap<*, *>) return false
        
        val map = other as SafeIterableMap<*, *>
        val i1 = iterator()
        val i2 = map.iterator()
        while (i1.hasNext() && i2.hasNext()) {
            val n1 = i1.next()
            val n2 = i2.next()
            if (n1 != null && n2 != null || (n1 != null && n1 != n2)) {
                return false
            }
        }
        
        return !i1.hasNext() && !i2.hasNext()
    }
    
    override fun hashCode(): Int {
        var h = 0
        iterator().forEach { h += it.hashCode() }
        return h
    }
    
    internal interface SupportRemove<K, V> {
        fun supportRemove(entry: Entry<K, V>)
    }
    
    inner class IteratorWithAdditions : Iterator<Map.Entry<K, V>>, SupportRemove<K, V> {
        private var mCurrent: Entry<K, V>? = null
        private var mBeforeStart = true
        override fun hasNext(): Boolean {
            if (mBeforeStart) {
                return mStart != null
            }
            return mCurrent?.mNext != null
        }
        
        override fun next(): Map.Entry<K, V> {
            if (mBeforeStart) {
                mBeforeStart = false
                mCurrent = mStart as Entry<K, V>
            } else {
                mCurrent = mCurrent?.mNext
            }
            return mCurrent!!
        }
        
        override fun supportRemove(entry: Entry<K, V>) {
            if (entry === mCurrent) {
                mCurrent = mCurrent?.mPrevious
                mBeforeStart = mCurrent == null
            }
        }
        
        
    }
    
    /**
     * 链表的节点
     */
    class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V> {
        
        var mNext: Entry<K, V>? = null
        var mPrevious: Entry<K, V>? = null
        
        override fun equals(other: Any?): Boolean {
            return when {
                other == this                          -> true
                null != other && other !is Entry<*, *> -> false
                else                                   -> {
                    val e = other as Entry<*, *>
                    this.key?.equals(e.key) == true && this.value?.equals(e.value) == true
                }
            }
        }
        
        override fun toString(): String {
            return "$key - $value"
        }
        
        override fun hashCode(): Int {
            return key.hashCode() xor value.hashCode()
        }
    }
    
    /**
     * 抽象的 列表迭代器
     */
    internal abstract class ListIterator<K, V>(
        var mNext: Entry<K, V>? = null, var mExpectedEnd: Entry<K, V>? = null
    ) : Iterator<Map.Entry<K, V>>, SupportRemove<K, V> {
        
        override fun hasNext(): Boolean {
            return mNext != null
        }
        
        override fun next(): Map.Entry<K, V> {
            val result = mNext!!
            mNext = nextNode()
            return result
        }
        
        override fun supportRemove(entry: Entry<K, V>) {
            if (mExpectedEnd == entry && entry == mNext) {
                mNext = null
                mExpectedEnd = null
            }
            if (mExpectedEnd == entry) {
                mExpectedEnd = backward(mExpectedEnd)
            }
            if (mNext == entry) {
                mNext = nextNode()
            }
        }
        
        private fun nextNode(): Entry<K, V>? {
            if (mNext == mExpectedEnd || mExpectedEnd == null) {
                return null
            }
            return forward(mNext)
        }
        
        abstract fun forward(entry: Entry<K, V>?): Entry<K, V>?
        
        abstract fun backward(entry: Entry<K, V>?): Entry<K, V>?
    }
    
    
    internal class AscendingIterator<K, V>(
        start: Entry<K, V>?, expectedEnd: Entry<K, V>?
    ) : ListIterator<K, V>(start, expectedEnd) {
        override fun forward(entry: Entry<K, V>?): Entry<K, V>? {
            return entry?.mNext
        }
        
        override fun backward(entry: Entry<K, V>?): Entry<K, V>? {
            return entry?.mPrevious
        }
    }
    
    internal class DescendingIterator<K, V>(
        start: Entry<K, V>?, expectedEnd: Entry<K, V>?
    ) : ListIterator<K, V>(start, expectedEnd) {
        override fun forward(entry: Entry<K, V>?): Entry<K, V>? {
            return entry?.mPrevious
        }
        
        override fun backward(entry: Entry<K, V>?): Entry<K, V>? {
            return entry?.mNext
        }
    }
}