package me.vinachiong.androidlib.jetpack.livedata;

/**
 * @author vina.chiong
 * @version v1.0.0
 */
public interface Observer<T> {

    void onChange(T t);
}
