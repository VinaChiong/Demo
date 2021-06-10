package me.vinachiong.jetpack.lifecycle;

/**
 * 【手动仿写Lifecycle系列】
 * 
 * LiveData的观察者接口
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public interface Observer<T> {

    /**
     * 观察数据变更
     * @param t
     */
    void onChange(T t);
}
