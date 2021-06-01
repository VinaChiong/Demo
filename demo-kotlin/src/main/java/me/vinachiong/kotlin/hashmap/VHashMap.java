package me.vinachiong.kotlin.hashmap;//package com.maniu.hashmap;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * @author vina.chiong
// * @version v1.0.0
// */
//public class VHashMap<K, V> {
//    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
//    static final int MAXIMUM_CAPACITY = 1 << 30;
//    static final int TREEIFY_THRESHOLD = 8;
//    static final int UNTREEIFY_THRESHOLD = 6;
//    private int capacity;
//
//    /* ---------------- Static utilities -------------- */
//    private int threshold;
//    private Node<K, V>[] table;
//    private int size;
//
//    static final int hash(Object key) {
//        int h;
//        return (null == key) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
//    }
//
//    static Class<?> comparableForClassFor(Object x) {
//        if (x instanceof Comparable) {
//            Class<?> c;
//            Type[] ts, as;
//            Type t;
//            ParameterizedType p;
//            if ((c = x.getClass()) == String.class)
//                return c;
//            if ((ts = c.getGenericInterfaces()) != null) {
//                for (int i = 0; i < ts.length; ++i) {
//                    if ((t = ts[i]) instanceof ParameterizedType &&
//                            ((p = (ParameterizedType) t).getRawType()
//                                    == Comparable.class) &&
//                            (as = p.getActualTypeArguments()) != null &&
//                            as.length == 1 && as[0] == c)
//                        return c;
//                }
//            }
//        }
//        return null;
//    }
//
//    static int compareComparables(Class<?> kc, Object k, Object x) {
//        return (x == null || x.getClass() != kc ? 0 :
//                ((Comparable) k).compareTo(x));
//    }
//
//    static final int tableSizeFor(int cap) {
//        int n = cap - 1;
//        n |= n >>> 1;
//        n |= n >>> 2;
//        n |= n >>> 4;
//        n |= n >>> 8;
//        n |= n >>> 16;
//
//        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
//    }
//
//    public V get(K key) {
//        Node<K, V> e;
//        return (e = getNode(hash(key), key)) == null ? null : e.value;
//    }
//
//    final Node<K, V> getNode(int hash, Object key) {
//        Node<K, V>[] tab;
//        Node<K, V> first, e;
//        int n;
//        K k;
//        if ((tab = table) != null && (n = tab.length) > 0
//                && (first = tab[(n - 1) & hash]) != null) {
//            // 先判断链表
//            if ((k = first.key) == key || (key != null && key.equals(k)))
//                return first;
//            if ((e = first.next) != null) {
//                // if (e instanceof TreeNode)
//                //   return ((TreeNode<K, V>)e).getTreeNode(hash, key);
//                do {
//                    if (e.hash == hash &&
//                            ((k = e.key) == key || key != null && key.equals(k)) &&)
//                        return e;
//                } while ((e = e.next) != null);
//            }
//        }
//        return null;
//    }
//
//    public V put(K key, V value) {
//        int h = hash(key);
//        return putVal(h, key, value, false, true);
//    }
//
//    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
//        Node<K, V>[] tab; Node<K, V> p; int n, i;
//        // 哈希表未初始化
//        if ((tab = table) == null || (n = tab.length) == 0)
//            n = (tab = resize()).length;
//        if ((p = tab[i = (n -1) & hash]) == null)
//            tab[i] = new Node(hash, key, value, null);
//        else {
//            Node<K, V> e; K k;
//            if (p.hash == hash &&
//                    ((k = p.key) == key || (key != null && key.equals(k))))
//                e = p;
////            else if (p instanceof TreeNode)
////                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
//            else {
//                for (int binCount = 0; ; ++binCount) {
//
//                }
//            }
//        }
//        return null;
//    }
//
//    final Node<K, V>[] resize() {
//
//    }
//
//    static final class Node<K, V> implements Map.Entry<K, V> {
//        int hash;
//        K key;
//        V value;
//        Node<K, V> next;
//
//        public Node(int hash, K key, V value, Node<K, V> next) {
//            this.hash = hash;
//            this.key = key;
//            this.value = value;
//            this.next = next;
//        }
//
//        public final int hashCode() {
//            return Objects.hashCode(key) ^ Objects.hashCode(value);
//        }
//
//        @Override
//        public K getKey() {
//            return key;
//        }
//
//        @Override
//        public V getValue() {
//            return value;
//        }
//
//        @Override
//        public V setValue(V v) {
//            V oldVal = this.value;
//            this.value = v;
//            return oldVal;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o instanceof Map.Entry) {
//                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
//                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()))
//                    return true;
//            }
//            return false;
//        }
//    }
//}
