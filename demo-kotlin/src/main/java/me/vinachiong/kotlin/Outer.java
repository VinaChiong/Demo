package me.vinachiong.kotlin;

/**
 * @author vina.chiong
 * @version v1.0.0
 */
public class Outer {
    public String a;
    public Inner mInner;


    public void genInner() {
        if (null == mInner) {
            Inner i = new Inner();
            mInner = i;
        }
    }

    public class Inner {
        String b;
    }

    public static class SInner {
        public Outer.Inner mInner;
        public Outer mOuter;
    }

    public interface HH {
        public void hh();
    }
}

