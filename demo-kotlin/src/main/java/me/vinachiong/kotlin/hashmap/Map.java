package me.vinachiong.kotlin.hashmap;
// 高  16 位 和 低 异或运算     1111
public interface Map<K, V> {

    // 向集合中插入数据
    public V put(K key, V value);

    // 根据k 从Map集合中查询元素
    public V get(K key);

    // 获取集合元素个数
    public int size();

    interface Entry<K, V> {
        K getKey();

        V getValue();

        V setValue(V value);
    }
}
