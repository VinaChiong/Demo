package me.vinachiong.kotlin.hashmap;//package com.maniu.hashmap;
//
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//
///**
// * 1.如何确定初始容量？
// * 2.hash算法是怎样的？
// * 3.hash值怎样求哈希表的索引
// * 4.每次新添加值：头插法，直接插在链头
// * 5.每个链表的长度限制?
// * 6.哈希表的扩容机制是怎样
// */
//public class FhashMap<K, V> {
//
//    static final int TREEIFY_THRESHOLD = 8;
//    private static final float DEFAULT_LOAD_FACTOR = 0.6f;
//    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
//    private static final int MAXIMUM_CAPACITY = 1 << 30; // 左移，低位补0
//    transient Node<K, V>[] table;
//    transient Set<Map.Entry<K, V>> entrySet;
//    transient int size;
//    transient int modCount;
//    private float loadFactor; // 扩容系数
//    private int threshold;
//    private HashMap<String, String> sample = new HashMap<>();
//
//    public FhashMap(int initialCapacity, float loadFactor) {
//        if (initialCapacity < 0)
//            throw new IllegalArgumentException("Illegal initialCapacity: " + initialCapacity);
//
//        if (initialCapacity > MAXIMUM_CAPACITY)
//            initialCapacity = MAXIMUM_CAPACITY;
//        if (loadFactor <= 0 || Float.isNaN(loadFactor))
//            throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);
//
//        this.loadFactor = DEFAULT_LOAD_FACTOR;
//
//        this.threshold = tableSizeFor(initialCapacity); // 计算初始长度
//
//    }
//
//    /**
//     * Returns x's Class if it is of the form "class C implements
//     * Comparable<C>", else null.
//     */
//    static Class<?> comparableClassFor(Object x) {
//        if (x instanceof Comparable) {
//            Class<?> c;
//            Type[] ts, as;
//            Type t;
//            ParameterizedType p;
//            if ((c = x.getClass()) == String.class) // bypass checks
//                return c;
//            if ((ts = c.getGenericInterfaces()) != null) {
//                for (int i = 0; i < ts.length; ++i) {
//                    if (((t = ts[i]) instanceof ParameterizedType) &&
//                            ((p = (ParameterizedType) t).getRawType() ==
//                                    Comparable.class) &&
//                            (as = p.getActualTypeArguments()) != null &&
//                            as.length == 1 && as[0] == c) // type arg is c
//                        return c;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Returns k.compareTo(x) if x matches kc (k's screened comparable
//     * class), else 0.
//     */
//    @SuppressWarnings({"rawtypes", "unchecked"}) // for cast to Comparable
//    static int compareComparables(Class<?> kc, Object k, Object x) {
//        return (x == null || x.getClass() != kc ? 0 :
//                ((Comparable) k).compareTo(x));
//    }
//
//
//    static int tableSizeFor(int capacity) {
//        // 如果 capacity = 10,
//        int n = capacity - 1; // n=9
//        //只看二进制低八位 0000_1010
//        // n>>>1  = 0000_0101; 所以 n | (n>>>1) = 0000_1111
//        // n>>>2  = 0000_0010; 所以 n | (n>>>2) = 0000_1111
//        // n>>>4  = 0000_0000; 所以 n | (n>>>4) = 0000_1111
//        // n>>>8  = 0000_0000; 所以 n | (n>>>8) = 0000_1111
//        // n>>>16 = 0000_0000; 所以 n | (n>>>16) = 0000_1111
//        n |= n >>> 1;
//        n |= n >>> 2;
//        n |= n >>> 4;
//        n |= n >>> 8;
//        n |= n >>> 16;
//        // 所以最后 n = 8 + 4 + 1 + 0 = 13
//
//        // 所以n其实是HashMap容量的边界的低值边
//
//        // 扩容算法的核心是每次扩展的「步伐数」。而二进制位移运算比起其他运算的效率是最高的
//        // 把期望容量capacity值 以二进制格式看待，向上找，比capacity大的最接近的的数
//        // 把最高位的1往低位全部填1，得出的结果：
//        // 既可以通过简单位移得出，又能保证了有足够的剩余空间，避免可能的重复扩容操作
//
//        return (n < 0) ? 1 : Math.min(n, MAXIMUM_CAPACITY);
//    }
//
//    // hash算法
//    static int hash(Object key) {
//        int h;
//        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
//    }
//
//    public void clear() {
//    }
//
//    public V put(K key, V value) {
//        return putVal(hash(key), key, value, false, true);
//    }
//
//    /**
//     * @param hash         hash
//     * @param key          key
//     * @param value        value
//     * @param onlyIfAbsent if true，不替换已存在的节点值
//     * @param evict        if false，哈希表在创建模式
//     * @return 变更后的值，否则null
//     */
//    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
//        Node<K, V>[] tab;
//        Node<K, V> node;
//        int n, i;
//        if ((tab = table) == null || (n = table.length) == 0)
//            n = (tab = resize()).length;
//
//        if ((node = tab[i = (n - 1) & hash]) == null)
//            //hash值在 哈希表的节点链表不存在
//            //需要初始化链表节点
//            tab[i] = newNode(hash, key, value, null);
//        else {
//            //hash值在 哈希表的节点链表 已存在
//            // 开始遍历
//            Node<K, V> e;
//            K k;
//
//            if (node.hash == hash &&
//                    ((k = node.key) == key || (key != null && key.equals(k))))
//                // 链表第一个节点就是目标节点
//                e = node;
//            else if (node instanceof TreeNode)
//                // 遍历的节点是TreeNode 红黑树
//                e = ((TreeNode<K, V>) node).putTreeVal(this, tab, hash, key, value);
//            else {
//                for (int bitCount = 0; ; ++bitCount) {
//                    if ((e = node.next) == null) {
//                        node.next = newNode(hash, key, value, null);
//                        // TREEIFY_THRESHOLD = 8, 当 bitCount 超出或等于8
//                        // 需要把当前的链表转化为红黑树结构
//                        if (bitCount >= TREEIFY_THRESHOLD - 1)
//                            treeifyBin(tab, hash);
//                        break;
//                    }
//                    if (e.hash == hash &&
//                            ((k = e.key) == key || (key != null && key.equals(k))))
//                        break;
//                    node = e;
//                }
//            }
//
//            if (e != null) {
//                V oldValue = e.value;
//                if (!onlyIfAbsent || oldValue == null)
//                    e.value = value;
//                afterNodeAccess(e);
//                return oldValue;
//            }
//        }
//        ++modCount;
//        if (++size > threshold)
//            resize();
//        afterNodeInsertion(evict);
//        return null;
//    }
//
//    final Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
//        return new Node<K, V>(hash, key, value, next);
//    }
//
//    final Node<K, V> replacementNode(Node<K, V> p, Node<K, V> next) {
//        return new Node<K, V>(p.hash, p.key, p.value, next);
//    }
//
//    final TreeNode<K, V> newTreeNode(int hash, K key, V value, Node<K, V> next) {
//        return new TreeNode<K, V>(hash, key, value, next);
//    }
//
//    final Node<K, V>[] resize() {
//        Node<K, V>[] oldTab = table;
//        int oldCap = (oldTab == null) ? 0 : oldTab.length;
//        int oldThr = threshold;
//        int newCap, newThr = 0;
//
//        if (oldCap > 0) {
//            if (oldCap > MAXIMUM_CAPACITY) {
//                threshold = Integer.MAX_VALUE;
//                return oldTab;
//            } else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
//                    oldCap >= DEFAULT_INITIAL_CAPACITY)
//                newThr = oldThr << 1;
//        } else if (oldThr > 0)
//            newCap = oldCap;
//        else {
//            newCap = DEFAULT_INITIAL_CAPACITY;
//            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
//        }
//        if (newThr == 0) {
//            float ft = (float) newCap * loadFactor;
//            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ? (int) ft : Integer.MAX_VALUE);
//        }
//        threshold = newThr;
//
//        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
//        table = newTab;
//
//        if (oldTab != null) {
//            for (int j = 0; j < oldCap; ++j) {
//                Node<K, V> e;
//                if ((e = oldTab[j]) != null) {
//                    oldTab[j] = null;
//                    if (e.next == null)
//                        newTab[e.hash & (newCap - 1)] = e;
//                    else if (e instanceof TreeNode)
//                        ((TreeNode<K, V>) e).split(this, newTab, j, oldCap);
//                    else {
//                        Node<K, V> loHead = null, loTail = null;
//                        Node<K, V> hiHead = null, hiTail = null;
//                        Node<K, V> next;
//
//                        do {
//                            next = e.next;
//                            if ((e.hash & oldCap) == 0) {
//                                if (loTail == null)
//                                    loHead = e;
//                                else
//                                    loTail.next = e;
//                                loTail = e;
//                            } else {
//                                if (hiTail == null)
//                                    hiHead = e;
//                                else
//                                    hiTail.next = e;
//                                hiTail = e;
//                            }
//                        } while ((e = next) != null);
//                        if (loTail != null) {
//                            loTail.next = null;
//                            newTab[j] = loHead;
//                        }
//                        if (hiTail != null) {
//                            hiTail.next = null;
//                            newTab[j + oldCap] = hiHead;
//                        }
//                    }
//                }
//            }
//        }
//
//        return newTab;
//    }
//
//    public V get(Object key) {
//        // sample.get()
//        Node<K, V> e;
//
//        return (e = getNode(hash(key), key)) == null ? null : e.value;
//    }
//
//    final Node<K, V> getNode(int hash, Object key) {
//        Node<K, V>[] tab;
//        Node<K, V> first, e;
//        int n;
//        K k;
//
//        if ((tab = table) != null && (n = tab.length) > 0 &&
//                (first = tab[(n - 1) & hash]) != null) {
//            // 哈希表不为null，且有值，可以遍历
//            if (first.hash == hash && // always check first node
//                    ((k = first.key) == key || key != null && key.equals(k))) {
//                return first;
//            }
//            if ((e = first.next) != null) {
//                // 如果是TreeNode类型
//                // if (first instanceof TreeNode)
//                //  return ((TreeNode<K, V>) first).getTreeNode(hash, key)
//
//                // 否则 遍历链表
//                do {
//                    if (e.hash == hash &&
//                            ((k = e.key) == key || (key != null && key.equals(k))))
//                        return e;
//                } while ((e = e.next) != null);
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 每个节点都有一个hash值
//     *
//     * @param <K>
//     * @param <V>
//     */
//    static class Node<K, V> implements Map.Entry<K, V> {
//        final int hash;
//        final K key;
//        V value;
//        Node<K, V> next; // 单向无循环链表
//
//        Node(int hash, K key, V value, Node<K, V> next) {
//            this.hash = hash;
//            this.key = key;
//            this.value = value;
//            this.next = next;
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
//        public V setValue(V newValue) {
//            V oldValue = value;
//            value = newValue;
//            return oldValue;
//        }
//
//        public final boolean equals(Object o) {
//            if (o == this) return true;
//            if (o instanceof Map.Entry) {
//                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
//                if (Objects.equals(key, e.getKey())
//                        && Objects.equals(value, e.getValue()))
//                    return true;
//            }
//            return false;
//        }
//    }
//
//    static class Entry<K, V> extends Node<K, V> {
//        Entry<K, V> before, after;
//
//        Entry(int hash, K key, V value, Node<K, V> next) {
//            super(hash, key, value, next);
//        }
//    }
//
//
//    /* ------------------------------------------------------------ */
//    // Tree bins
//
//    /**
//     * 树箱子中的条目。继承LinkedHashMap.Entry，本质是继承了Node类型。
//     * 可以被用作 哈希表 中的节点
//     */
//    static final class TreeNode<K, V> extends Entry<K, V> {
//        TreeNode<K, V> parent; // 红黑树链接
//        TreeNode<K, V> left;
//        TreeNode<K, V> right;
//        TreeNode<K, V> prev; // 删除操作的时候需要解除链接
//        boolean red;
//
//        TreeNode(int hash, K key, V value, Node<K, V> next) {
//            super(hash, key, value, next);
//        }
//
//        /**
//         * 确保当前的树节点是哈希表中的bin的第一个节点
//         */
//        static <K, V> void moveRootToFront(Node<K, V>[] tab,
//                                           TreeNode<K, V> root) {
//            int n;
//            // 哈希表长度不为0，root不空
//            if (root != null && tab != null && (n = tab.length) > 0) {
//                int index = (n - 1) & root.hash; // 计算root节点在哈希表的索引
//                TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
//                if (root != first) { // root不是哈希表中对应索引链表的第一个节点
//                    // 可能情况:
//                    // tab[index] -> first -> ... rp -> root -> rn -> ...
//
//                    Node<K, V> rn;
//                    tab[index] = root; // 1.哈希表索引 tab[index]先先指向链表的root节点
//
//                    // 2.把 root 从链表中拿出来，并吧 rp -> rn 接上关系
//                    TreeNode<K, V> rp = root.prev;
//                    if ((rn = root.next) != null)
//                        ((TreeNode<K, V>) rn).prev = rp;
//                    if (rp != null)
//                        rp.next = rn;
//
//                    // 3.把原来链头first放在root.next，并补充关系
//                    if (first != null)
//                        first.prev = root;
//                    root.next = first;
//                    root.prev = null;
//
//                    // 4.最后结果:
//                    // tab[index] -> root -> first -> ... rp -> rn -> ...
//                }
//                assert checkInvariants(root);
//            }
//        }
//
//        // Tie-breaking工具方法：当hashcode无法比较时候，保证按顺序插入。
//        // 不需要所有的排序，只需要明确的规则维护equivalence across rebalancings。
//        //
//        static int tieBreakOrder(Object a, Object b) {
//            int d;
//            if (a == null || b == null ||
//                    (d = a.getClass().getName()
//                            .compareTo(b.getClass().getName())) == 0)
//                d = (System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1);
//            return d;
//        }
//
//        // 左转: 为公平插入使用
//        static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root,
//                                                TreeNode<K, V> p) {
//            // 涉及至多4层及。目标是把 p和p的右节点r的：
//            // - 父子关系互换，左右关系互换
//            // - p的右节点变为r的左节点rl
//            // - p的父节点pp，变为r的父节点
//            TreeNode<K, V> r, pp, rl;
//            if (p != null && (r = p.right) != null) {
//                if ((rl = p.right = r.left) != null)
//                    rl.parent = p;
//                if ((pp = r.parent = p.parent) == null)
//                    (root = r).red = false; // 该节点是黑树
//                else if (pp.left == p)
//                    pp.left = r;
//                else
//                    pp.right = r;
//                r.left = p;
//                p.parent = r;
//            }
//            return root;
//        }
//
//        // 右转：为公平插入、公平删除使用
//        static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root,
//                                                 TreeNode<K, V> p) {
//            TreeNode<K, V> l, pp, lr;
//            if (p != null && (l = p.left) != null) {
//                if ((lr = p.left = l.right) != null)
//                    lr.parent = p;
//                if ((pp = l.parent = p.parent) == null)
//                    (root = l).red = false;
//                else if (pp.right == p)
//                    pp.right = l;
//                else
//                    pp.left = l;
//                l.right = p;
//                p.parent = l;
//            }
//            return root;
//        }
//
//        static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root,
//                                                      TreeNode<K, V> x) {
//            x.red = true;
//            for (TreeNode<K, V> xp, xpp, xppl, xppr; ; ) {
//                if ((xp = x.parent) == null) {
//                    x.red = false;
//                    return x;
//                } else if (!xp.red || (xpp = xp.parent) == null)
//                    return root;
//                if (xp == (xppl = xpp.left)) {
//                    if ((xppr = xpp.right) != null && xppr.red) {
//                        xppr.red = false;
//                        xp.red = false;
//                        xpp.red = true;
//                        x = xpp;
//                    } else {
//                        if (x == xp.right) {
//                            root = rotateLeft(root, x = xp);
//                            xpp = (xp = x.parent) == null ? null : xp.parent;
//                        }
//                        if (xp != null) {
//                            xp.red = false;
//                            if (xpp != null) {
//                                xpp.red = true;
//                                root = rotateRight(root, xpp);
//                            }
//                        }
//                    }
//                } else {
//                    if (xppl != null && xppl.red) {
//                        xppl.red = false;
//                        xp.red = false;
//                        xpp.red = true;
//                        x = xpp;
//                    } else {
//                        if (x == xp.left) {
//                            root = rotateRight(root, x = xp);
//                            xpp = (xp = x.parent) == null ? null : xp.parent;
//                        }
//                        if (xp != null) {
//                            xp.red = false;
//                            if (xpp != null) {
//                                xpp.red = true;
//                                root = rotateLeft(root, xpp);
//                            }
//
//                        }
//                    }
//                }
//            }
//        }
//
//        static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root,
//                                                     TreeNode<K, V> x) {
//            for (TreeNode<K, V> xp, xpl, xpr; ; ) {
//                if (x == null || x == root)
//                    return root;
//                else if ((xp = x.parent) == null) {
//                    x.red = false;
//                    return x;
//                } else if (x.red) {
//                    x.red = false;
//                    return root;
//                } else if ((xpl = xp.left) == x) {
//                    if ((xpr = xp.right) != null && xpr.red) {
//                        xpr.red = false;
//                        xp.red = true;
//                        root = rotateLeft(root, xp);
//                        xpr = (xp = x.parent) == null ? null : xp.right;
//                    }
//                    if (xpr == null)
//                        x = xp;
//                    else {
//                        TreeNode<K, V> sl = xpr.left, sr = xpr.right;
//                        if ((sr == null || !sr.red) &&
//                                (sl == null || !sl.red)) {
//                            xpr.red = true;
//                            x = xp;
//                        } else {
//                            if (sr == null || !sr.red) {
//                                if (sl != null)
//                                    sl.red = false;
//                                xpr.red = true;
//                                root = rotateRight(root, xpr);
//                                xpr = (xp = x.parent) == null ?
//                                        null : xp.right;
//                            }
//                            if (xpr != null) {
//                                xpr.red = (xp == null) ? false : xp.red;
//                                if ((sr = xpr.right) != null)
//                                    sr.red = false;
//                            }
//                            if (xp != null) {
//                                xp.red = false;
//                                root = rotateLeft(root, xp);
//                            }
//                            x = root;
//                        }
//                    }
//                } else { // symmetric
//                    if (xpl != null && xpl.red) {
//                        xpl.red = false;
//                        xp.red = true;
//                        root = rotateRight(root, xp);
//                        xpl = (xp = x.parent) == null ? null : xp.left;
//                    }
//                    if (xpl == null)
//                        x = xp;
//                    else {
//                        TreeNode<K, V> sl = xpl.left, sr = xpl.right;
//                        if ((sl == null || !sl.red) &&
//                                (sr == null || !sr.red)) {
//                            xpl.red = true;
//                            x = xp;
//                        } else {
//                            if (sl == null || !sl.red) {
//                                if (sr != null)
//                                    sr.red = false;
//                                xpl.red = true;
//                                root = rotateLeft(root, xpl);
//                                xpl = (xp = x.parent) == null ?
//                                        null : xp.left;
//                            }
//                            if (xpl != null) {
//                                xpl.red = (xp == null) ? false : xp.red;
//                                if ((sl = xpl.left) != null)
//                                    sl.red = false;
//                            }
//                            if (xp != null) {
//                                xp.red = false;
//                                root = rotateRight(root, xp);
//                            }
//                            x = root;
//                        }
//                    }
//                }
//            }
//        }
//
//        /**
//         * Recursive invariant check
//         * 递归地检查不变性
//         */
//        static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
//            TreeNode<K, V> tp = t.parent,
//                    tl = t.left,
//                    tr = t.right,
//                    tb = t.prev,
//                    tn = (TreeNode<K, V>) t.next;
//            if (tb != null && tb.next != t)
//                return false;
//            if (tn != null && tn.prev != t)
//                return false;
//            if (tp != null && t != tp.left && t != tp.right)
//                return false;
//            if (tl != null && (tl.parent != t || tl.hash > t.hash))
//                return false;
//            if (tr != null && (tr.parent != t || tr.hash < t.hash))
//                return false;
//            if (t.red && tl != null && tl.red && tr != null && tr.red)
//                return false;
//            if (tl != null && !checkInvariants(tl))
//                return false;
//            if (tr != null && !checkInvariants(tr))
//                return false;
//            return true;
//        }
//
//        /**
//         * 返回树的根节点
//         */
//        final TreeNode<K, V> root() {
//            // 从当前节点，往上找父节点，直到parent = null，则这时候的节点就是根节点
//            for (TreeNode<K, V> r = this, p; ; ) {
//                if ((p = r.parent) == null)
//                    return r;
//                r = p;
//            }
//        }
//
//        /**
//         * 根据传入的hash和key值从根节点p开始查找目标节点
//         * 参数kc缓存 comparableClassFor(key) upon first use comparing keys.
//         */
//        final TreeNode<K, V> find(int h, Object k, Class<?> kc) {
//            TreeNode<K, V> p = this;
//            do {
//                int ph, dir;
//                K pk;
//
//                TreeNode<K, V> pl = p.left, pr = p.right, q;
//
//                if ((ph = p.hash) > h)
//                    p = pl;
//                else if (ph < h)
//                    p = pr;
//                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
//                    return p;
//                else if (pl == null)
//                    p = pr;
//                else if (pr == null)
//                    p = pl;
//                else if ((kc != null ||
//                        (kc = comparableClassFor(k)) != null) &&
//                        (dir = compareComparables(kc, k, pk)) != 0)
//                    p = (dir < 0) ? pl : pr;
//                else if ((q = pr.find(h, k, kc)) != null)
//                    return q;
//            } while (p != null);
//            return null;
//        }
//
//        /**
//         * 确保从根节点开始查询。表示在整棵树内查询。如果当前是根节点，就更快
//         */
//        final TreeNode<K, V> getTreeNode(int h, Object k) {
//            return ((parent != null) ? root() : this).find(h, k, null);
//        }
//
//        /* ------------------------------------------------------------ */
//        // Red-black tree methods, all adapted from CLR
//
//        /**
//         * 把当前链接到当前节点的所有节点，格式化为红黑树
//         *
//         * @param tab
//         */
//        final void treeify(Node<K, V>[] tab) {
//            TreeNode<K, V> root = null;
//            for (TreeNode<K, V> x = this, next; x != null; x = next) {
//                next = (TreeNode<K, V>) x.next;
//                x.left = x.right = null;
//                if (root == null) {
//                    x.parent = null;
//                    x.red = false;
//                    root = x;
//                } else {
//                    K k = x.key;
//                    int h = x.hash;
//                    Class<?> kc = null;
//                    for (TreeNode<K, V> p = root; ; ) {
//                        int dir, ph;
//                        K pk = p.key;
//                        if ((ph = p.hash) > h)
//                            dir = -1;
//                        else if (ph < h)
//                            dir = 1;
//                        else if ((kc == null &&
//                                (kc = comparableClassFor(k)) == null) ||
//                                (dir = compareComparables(kc, k, pk)) == 0)
//                            dir = tieBreakOrder(k, pk);
//
//                        TreeNode<K, V> xp = p;
//                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
//                            x.parent = xp;
//                            if (dir <= 0)
//                                xp.left = x;
//                            else
//                                xp.right = x;
//                            root = balanceInsertion(root, x);
//                            break;
//                        }
//                    }
//                }
//            }
//            moveRootToFront(tab, root);
//        }
//
//        /**
//         * list
//         *
//         * @param map
//         */
//        final Node<K, V> untreeify(FhashMap<K, V> map) {
//            Node<K, V> hd = null, tl = null;
//            for (Node<K, V> q = this; q != null; q = q.next) {
//                Node<K, V> p = map.replacementNode(q, null);
//                if (tl == null)
//                    hd = p;
//                else
//                    tl.next = p;
//                tl = p;
//            }
//            return hd;
//        }
//
//        /**
//         * 红黑树版本的putVal
//         */
//        final TreeNode<K, V> putTreeVal(FhashMap<K, V> map, Node<K, V>[] tab,
//                                        int h, K k, V v) {
//            Class<?> kc = null;
//            boolean searched = false;
//            TreeNode<K, V> root = (parent != null) ? root() : this;
//            for (TreeNode<K, V> p = root; ; ) {
//                int dir, ph;
//                K pk;
//                if ((ph = p.hash) > h)
//                    dir = -1;
//                else if (ph < h)
//                    dir = -1;
//                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
//                    return p;
//                else if ((kc == null &&
//                        (kc = comparableClassFor(k)) == null) ||
//                        (dir = compareComparables(kc, k, pk)) == 0) {
//                    if (!searched) {
//                        TreeNode<K, V> q, ch;
//                        searched = true;
//                        if (((ch = p.left) != null &&
//                                (q = ch.find(h, k, kc)) != null) ||
//                                ((ch = p.right) != null &&
//                                        (q = ch.find(h, k, kc)) != null))
//                            return q;
//                    }
//                    dir = tieBreakOrder(k, pk);
//                }
//
//                TreeNode<K, V> xp = p;
//                if ((p = (dir <= 0) ? p.left : p.right) == null) {
//                    Node<K, V> xpn = xp.next;
//                    TreeNode<K, V> x = map.newTreeNode(h, k, v, xpn);
//                }
//
//            }
//        }
//
//        /**
//         * 把当前的树节点从树中移除
//         * 在map中，移除链表数组tab中包含的Node类型链表，且这些链表是已经存在与map的。
//         * 这个操作比典型的红黑树删除代码更复杂混乱。因为我们不能交换（被next指向的）节点的内容。
//         * 取而代之，我们只能交还树的链接。
//         *
//         * @param map
//         * @param tab
//         * @param movable
//         */
//        final void removeTreeNode(FhashMap<K, V> map, Node<K, V>[] tab,
//                                  boolean movable) {
//            int n;
//            if (tab == null || (n = tab.length) == 0)
//                return;
//            int index = (n - 1) & hash;
////            TreeNode<K, V> first = (TreeNode<K, V>)
//        }
//
//        /**
//         * 把哈希树中一个盒子内的节点，拆分成底层和上层的tree黑子
//         * 褪树化如果节点数低于阈值。仅仅能在resize()被调用
//         *
//         * @param map
//         * @param tab
//         * @param index
//         * @param bit
//         */
//        final void split(FhashMap<K, V> map, Node<K, V>[] tab, int index, int bit) {
//            TreeNode<K, V> b = this;
//            // Relink into lo and hi lists, preserving order
//            TreeNode<K, V> loHead = null, loTail = null;
//            TreeNode<K, V> hiHead = null, hiTail = null;
//        }
//    }
//}
