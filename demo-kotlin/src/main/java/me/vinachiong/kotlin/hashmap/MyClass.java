package me.vinachiong.kotlin.hashmap;

import java.util.ArrayList;


public class MyClass {
    public static void main(String[] args) {
        FakeArrayMap<String, String> map = new FakeArrayMap<String, String>(8);

        map.put("我", "艹");
        map.put("泥", "马");
        map.put("那", "隔");
        map.put("壁", "！");


        map.print();

        new ArrayList<String>();
//        MyHashMap myHashMap = new MyHashMap<String, String>(14, 0.75f);
//        myHashMap.put("1号", "苍井空");// 0
//        myHashMap.put("2号", "林志玲");// 1
//        myHashMap.put("3号", "林雯雯");// 2
//        myHashMap.put("4号", "4号");// 3
//        myHashMap.put("6号", "6号");// 4
//        myHashMap.put("7号", "7号");//5
//        myHashMap.put("14号", "14号");//6
//
//        myHashMap.put("22号", "22号");//7
//        myHashMap.put("26号", "26号");//8
//        myHashMap.put("27号", "27号");//9
//        myHashMap.put("28号", "28号");//10
//        myHashMap.put("66号", "66");//11
//        myHashMap.put("28号", "28号");//10
//        myHashMap.put("67号", "66");//11
//        myHashMap.put("69号", "66");//11
//
//
//
//        myHashMap.put("111号", "22号");//7
//        myHashMap.put("112号", "26号");//8
//        myHashMap.put("113号", "27号");//9
//        myHashMap.put("114号", "28号");//10
//        myHashMap.put("116号", "66");//11
//        myHashMap.put("118号", "28号");//10
//        myHashMap.put("119号", "66");//11
//        myHashMap.put("123号", "66");//11
////        扩容 机制        16  *0.75    12   > 32个
//        myHashMap.print();

    }
}
