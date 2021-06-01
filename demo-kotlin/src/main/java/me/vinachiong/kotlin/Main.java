package me.vinachiong.kotlin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vina.chiong
 * @version v1.0.0
 */

public class Main {
    public static void main(String[] a) {
        Outer outer = new Outer();
        final Outer.Inner inner = outer.new Inner();
        Outer.SInner sInner = new Outer.SInner();

        outer.mInner = inner;
        Outer.HH hh1 = () -> {
            System.out.println("inner = " + inner.toString());
        };
        Happy hh2 = new Happy() {
            @Override
            void hey() {

            }
        };

        System.out.println("inner:" + inner.getClass().getName());
        System.out.println("sInner:" + sInner.getClass().getName());
        System.out.println("hh1:" + hh1.getClass().getName());
        System.out.println("hh2:" + hh2.getClass().getName());

    }

    public void upperBound() {
        Plate<? extends Fruit> p = new Plate<>(new Apple());
        List<Fruit> list = new ArrayList<>();
        //不能存入任何元素
//        p.set(new Fruit());    //Error
//        p.set(new Apple());    //Error

        //读取出来的东西只能存放在Fruit或它的基类里。
        Fruit newFruit1 = p.get();
        Object newFruit2 = p.get();
        // Apple newFruit3=p.get();    //Error
    }

    public void lowerBound() {
        Plate<? super Fruit> p = new Plate<Fruit>(new Fruit());
        //存入元素正常
        p.set(new Fruit());
        p.set(new Apple());
        p.set(new GreenApple());

        //读取出来的东西只能存放在Object类里。
        // Apple newFruit3=p.get();    //Error
        // Fruit newFruit1=p.get();    //Error
        Object newFruit2 = p.get();
    }

    //Lev 1
    class Food { }
    //Lev 2
    class Fruit extends Food { }
    class Meat extends Food {}
    //Lev 3
    class Apple extends Fruit { }
    class Banana extends Fruit { }

    class Pork extends Meat { }
    class Beef extends Meat { }
    //Lev 4
    class RedApple extends Apple { }
    class GreenApple extends Apple { }

    class Plate<T> {
        private T item;

        public Plate(T t) {
            item = t;
        }

        public void set(T t) {
            item = t;
        }

        public T get() {
            return item;
        }
    }



    class A {}
    class B extends A {}
    class C extends B {}
    class D extends C {}
    interface Source<X, Y> {
        X next(Y y);
    }

    void demo(Source<? extends B, ? super B> source) {

    }
    void demo2() {
//        Source<A, C> ac = null;
//        demo(ac);

        Source<C, B> cb = null;
        demo(cb);
        Source<D, A> da = null;
        demo(da);
    }
}