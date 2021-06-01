package me.vinachiong.kotlin.hashmap;

public class MyHashMap<K,V>  implements  Map<K,V>{
//     扩冲
    float loadFactor = 0.75f;
    //16  1<<4
    static int threshold = 0;
    Node[] table = null;
    // 2. 实际用到table 存储容量 大小
    int size;
    public MyHashMap() {
        threshold = 1 << 4;
    }

//   5 %3   效果差
//    位运算  15
//     2   4   8  16   32
//    14   hashmap     查找速度是最有
    public MyHashMap(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        threshold = tableSizeFor(initialCapacity);
    }
//    数组容量的确定   hash
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return  n + 1;
    }


    @Override
    public V put(K key, V value) {
        synchronized (MyHashMap.class) {
            if (table == null) {
                table = new Node[threshold];
            }
        }
        if(size>=(threshold*loadFactor)){
            //进行扩容
            resize();

        }

//            应该存放的索引值  索引     异或     与     下标
            int index = hash(key) & (threshold-1);

//             空 1   不为空  2
            Node<K, V> node = table[index];
//            A 线程  B线程
            if (node == null) {
                node = new Node<K, V>(key, value, null);
                size++;
            }else {
//
//                node不为   david
                //取多一个值节点
                Node<K, V> newNode = node;
                while(newNode!=null){
//                     node   key  单向链表

                    if(newNode.getKey().equals(key)){
                        return newNode.setValue(value);
                    }else {
                        //若该到了链表最后的节点为空，则将新的节点添加到链表的头部
                        if(newNode.next==null){
//                            新node  river    node  david
                            node = new Node<K, V>(key, value, node);
                            size++;
                        }
                    }

                    newNode=newNode.next;
                }
            }
            //将该节点存放到index下标位置
            table[index]=node;




        return null;
    }
//   什么时候扩容  扩容的机制是怎么
    private void resize() {
        System.out.printf("扩容中");
//
        threshold = threshold << 1;

        Node<K, V>[] newtable = new Node[threshold];
//        遍历之前的旧数据
        for(int i = 0;i<table.length;i++){
            //1.重新计算index 获取扩容后的下标值
            Node<K, V> oldNode = table[i];
//            单向链表的遍历  红黑树     ----》 搜索   红黑树  不需要  导弹---》目标   演进   树   1000技师
            while(oldNode!=null){
                //2.保存 下个节点  oldNode  david
                Node oldnext=oldNode.next;
                int index = hash(oldNode.getKey())& (newtable.length-1);
//            应该  效率固然 高  查找失误   oldNode =david
//                oldNode   river老师   ----》  oldnext   david
//       可能  有值  第一次 没有值    有值   river
               oldNode.next= newtable[index];
                //4.将重新链接好的链表  覆盖到  数组index对应的位置
                newtable[index]=oldNode;
                oldNode=oldnext;
            }
//            table
            table[i] = null;
        }
        //执行完后，将newtable覆盖到原table，减少内存的占用
        table=newtable;
        threshold = newtable.length;
        newtable = null;// 将 对象变为不可达对象  垃圾回收
    }

    @Override
    public V get(K key) {

//        遍历
        getNode(table[hash(key.hashCode()) & (threshold - 1)], key);

        return null;
    }
    public Node<K, V> getNode(Node<K, V> node,K k){
//         遍历链表
        while(node!=null){
            if(node.getKey().equals(k)||node.getKey()==k){
                return node;
            }
            node=node.next;
        }
        return null;
    }
    @Override
    public int size() {
        return 0;
    }
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    public void print() {
        for (int i = 0; i < table.length; i++) {
            Node<K, V> node = table[i];
            System.out.print("下标位置[" + i + "]");
            while (node != null) {
                System.out.print("[ key:" + node.getKey() + ",value:" + node.getValue() + "]");
                node = node.next;

            }
            System.out.println();
        }
    }


    //    查找 和插入速度最优
//节点
    class Node<K, V> implements Entry<K, V> {
        private K key;
        private V value;

//        单项链表
        private Node<K,V> next;
        public Node(K key, V value, Node<K, V> next) {
            super();
            this.key = key;
            this.value = value;
            this.next = next;
        }
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
//            复写
            V oldVlue = this.value;
            this.value = value;
            return oldVlue;
        }
    }
}
