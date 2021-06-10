package me.vinachiong.jetpack.lifecycle

import me.vinachiong.jetpack.core.FastSafeIterableMap
import java.lang.ref.WeakReference

/**
 * 【手动仿写Lifecycle系列】
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class LifecycleRegistry(
    owner: LifecycleOwner
): Lifecycle() {
    
    private var mCurrentState: State = State.INITIALIZED
    private var mHandlingEvent = false // 是否正在处理 Event
    private val lifecycleOwner = WeakReference(owner)
    private val mObserverMap = FastSafeIterableMap<LifecycleObserver, ObserverWithState>()
    
    // 用于新添加Observer时候，进行State提升。记录循环中上一个State
    private val mParentStates = mutableListOf<State>()
    
    override fun addObserver(observer: LifecycleObserver) {
        val initialState = if (mCurrentState == State.DESTROYED) State.DESTROYED else State.INITIALIZED
        val statefulObserver = ObserverWithState(initialState, observer)
        val previous = mObserverMap[observer]
    
        if (previous != null) return
    
        lifecycleOwner.get()?:return
        
        // 通过比对 statefulObserver.state与当前 state，并做逐一事件转发处理
        val targetState = calculateTargetState(observer)
        while ((statefulObserver.mState < targetState && mObserverMap.contains(observer))) {
        
        }
    }
    
    private fun calculateTargetState(observer: LifecycleObserver): State {
        val previous = mObserverMap.ceil(observer)
        val siblingState = previous?.value?.mState
        val parentState = mParentStates.lastOrNull()
        return min(min(mCurrentState, siblingState), parentState)
    }
    
    override fun removeObserver(observer: LifecycleObserver) {
        mObserverMap.remove(observer)
    }
    
    override fun getCurrentState(): State = mCurrentState
    
    fun handleLifecycleEvent(event: Event) {
        // 根据event，计算下一个要进入的State
        moveToState(event.targetState)
    }
    
    private fun moveToState(state: State) {
        if (state == mCurrentState) {
            return
        }
        mCurrentState = state
    
        
        mHandlingEvent = true
        sync()
        mHandlingEvent = false
    }
    
    private fun sync() {
    
    }
    
    companion object {
        
        internal fun min(state1: State, state2: State?): State {
            return if (state2 != null && state2 < state1) state2 else state1
        }
    
        internal class ObserverWithState(
            var mState: State,
            val mLifecycleObserver: LifecycleObserver
        ) {
            fun dispatchEvent(owner: LifecycleOwner, event: Event) {
                val newState = event.targetState
                mState = min(mState, newState)
                if (mLifecycleObserver is LifecycleEventObserver) {
                    mLifecycleObserver.onStateChanged(owner, event)
                }
                mState = newState
            }
        }
    }
}