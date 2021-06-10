package me.vinachiong.jetpack.lifecycle

/**
 * 【手动仿写Lifecycle系列】
 *
 * 用于运行相关lifecycle组件
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class MockLifecycleOwner: LifecycleOwner {
    
    val mLifecycle = LifecycleRegistry(this)
    
    override fun getLifecycle(): Lifecycle {
        return mLifecycle
    }
    
    fun onCreate() {
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        mLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
    
}