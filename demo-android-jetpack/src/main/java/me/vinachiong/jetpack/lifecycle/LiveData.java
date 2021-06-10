package me.vinachiong.jetpack.lifecycle;


import java.util.HashMap;

import static me.vinachiong.jetpack.lifecycle.Lifecycle.State.DESTROYED;

/**
 * 存储值，并且支持通知，值的观察者，值的变更情况
 * 支持在任何线程更新LiveData上的值，并通知观察者
 * 观察者一定是在"main"线程接收到通知
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public abstract class LiveData<T> {
    static final int START_VERSION = -1;
    static final Object NOT_SET = new Object();
    final Object mDataLock = new Object();
    private HashMap<Observer<? super T>, ObserverWrapper> mObservers = new HashMap<>();
    private int mActiveCount;

    private volatile Object mData;
    private Object mPendingData;
    private int mVersion;

    private boolean mChangingActiveState;
    private boolean mDispatchingValue;
    private boolean mDispatchInvalidated;

    private final Runnable mPostValueRunnable = new Runnable() {
        @Override
        public void run() {
            Object newValue;
            synchronized (mDataLock) {
                newValue = mPendingData;
                mPendingData = NOT_SET;
            }
            setValue((T) newValue);
        }
    };

    protected void postValue(T value) {
        boolean postTask;
        synchronized (mDataLock) {
            postTask = mPendingData == NOT_SET;
            mPendingData = value;
        }
        if (!postTask) {
            return;
        }
        //TODO 切换到主线程执行
    }

    protected void setValue(T value) {
        // assertMainThread("setValue");
        mVersion++;
        mData = value;
        dispatchingValue(null);
    }

    //    @SuppressWarnings("unchecked")
    private void considerNotify(ObserverWrapper observer) {
        if (!observer.mActive) { // 观察者不可用
            return;
        }

        // 在发送value前先检查最新的state。 有可能已经变化但事件未触发
        // 首先检查 observer.active 作用事件入口。如果状态已边但未接受到事件，
        // 稳妥起见，我们最好不要发送新value给observer
        if (!observer.shouldBeActive()) {
            observer.activeStateChanged(false);
            return;
        }
        if (observer.mLastVersion >= mVersion) {
            // 判断观察者上获得的value的版本号，与本地的版本号对比
            return;
        }
        observer.mLastVersion = mVersion;
        observer.mObserver.onChange((T) mData);
    }

    /**
     * setValue()被调用
     * 或者Observer观察者的activeStateChanged(true)方法被调用
     * 时候触发本方法
     *
     * @param initiator
     */
    void dispatchingValue(ObserverWrapper initiator) {
        if (mDispatchingValue) {
            // 如果正在dispatching value
            // 则让后面的for()循环中止，并重置do-while循环
            mDispatchInvalidated = true;
            return;
        }
        mDispatchingValue = true;
        do {
            mDispatchInvalidated = false;
            if (initiator != null) {
                considerNotify(initiator);
                initiator = null;
            } else {
//                // 遍历Observer链表，判断观察者状态并发送newValue
//                for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
//                     mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
//                    considerNotify(iterator.next().getValue());
//                    if (mDispatchInvalidated) {
//                        break;
//                    }
//                }
            }
        } while (mDispatchInvalidated);
        mDispatchingValue = false;
    }

    /**
     * 通过LifecycleOwner与Observer绑定，成为新的监听者ObserserWrapper
     *
     * @param owner    owner
     * @param observer observer
     */
    public void observe(LifecycleOwner owner, Observer<? super T> observer) {
        // assertMainThread
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            return; // 生命周期对象的状态未已结束，没有绑定必要
        }
        LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
        ObserverWrapper existing = mObservers.get(observer);
        if (null == existing) {
            mObservers.put(observer, wrapper);
        }

        if (null != existing && !existing.isAttachedTo(owner)) {
            throw new IllegalArgumentException("一个Observer不能同时与多个lifecycle对象绑定使用");
        }
        if (existing != null) {
            return; // 已存在 不重复绑定
        }
        owner.getLifecycle().addObserver(wrapper); // 让观察者 真正"观察"
    }

    public void observerForever(Observer<? super T> observer) {
        AlwaysActiveObserver wrapper = new AlwaysActiveObserver(observer);
        ObserverWrapper existing = mObservers.get(observer);
        if (null == existing) mObservers.put(observer, wrapper);

        if (existing instanceof LiveData.LifecycleBoundObserver) {
            throw new IllegalArgumentException("一个Observer不能同时与多个lifecycle对象绑定使用");
        }
        if (null != existing) return;

        wrapper.activeStateChanged(true);
    }

    public void removeObserver(Observer<? super T> observer) {
        // assertMainThread
        ObserverWrapper removed = mObservers.remove(observer);
        if (null == removed) return;

        removed.detachObserver();
        removed.activeStateChanged(false);
    }

    public void removeObservers(final LifecycleOwner owner) {
        assertMainThread("removeObservers");
        for (Observer<? super T> key : mObservers.keySet()) {
            ObserverWrapper wrapper = mObservers.get(key);
            if (wrapper.isAttachedTo(owner)){
                removeObserver(key);
            }
        }
    }

    void changeActiveCounter(int change) {
        int previousActiveCount = mActiveCount;
        mActiveCount += change;
        if (mChangingActiveState) return;
        mChangingActiveState = true;

        try {
            while (previousActiveCount != mActiveCount) {
                boolean needToCallActive = previousActiveCount == 0 && mActiveCount > 0;
                boolean needToCallInactive = previousActiveCount > 0 && mActiveCount == 0;
                previousActiveCount = mActiveCount;
                if (needToCallActive) {
                    onActive();
                } else if (needToCallInactive) {
                    onInactive();
                }
            }
        } finally {
            mChangingActiveState = false;
        }
    }

    protected void onActive() {

    }

    protected void onInactive() {

    }

    private final class LifecycleBoundObserver extends ObserverWrapper implements LifecycleEventObserver {
        final LifecycleOwner mOwner;

        LifecycleBoundObserver(LifecycleOwner owner, Observer<? super T> observer) {
            super(observer);
            this.mOwner = owner;
        }

        @Override
        boolean shouldBeActive() {
            return mOwner.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
        }

        @Override
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            Lifecycle.State currentState = mOwner.getLifecycle().getCurrentState();
            if (currentState == DESTROYED) {
                removeObserver(mObserver);
                return;
            }
            Lifecycle.State prevState = null;
            while (prevState != currentState) {
                prevState = currentState;
                activeStateChanged(shouldBeActive());
                currentState = mOwner.getLifecycle().getCurrentState();
            }
        }

        @Override
        boolean isAttachedTo(LifecycleOwner owner) {
            return mOwner == owner;
        }

        @Override
        void detachObserver() {
            mOwner.getLifecycle().removeObserver(this);
        }
    }

    private abstract class ObserverWrapper {
        final Observer<? super T> mObserver;
        boolean mActive;
        int mLastVersion = START_VERSION;

        ObserverWrapper(Observer<? super T> observer) {
            mObserver = observer;
        }

        abstract boolean shouldBeActive();

        boolean isAttachedTo(LifecycleOwner owner) {
            return false;
        }

        void detachObserver() {
        }

        void activeStateChanged(boolean newActive) {
            if (newActive == mActive) {
                return;
            }
            // immediately set active state, so we'd never dispatch anything to inactive
            // owner
            mActive = newActive;
            changeActiveCounter(mActive ? 1 : -1);
            if (mActive) {
                dispatchingValue(this);
            }
        }
    }

    private class AlwaysActiveObserver extends ObserverWrapper {

        AlwaysActiveObserver(Observer<? super T> observer) {
            super(observer);
        }

        @Override
        boolean shouldBeActive() {
            return true;
        }
    }

    static void assertMainThread(String methodName) {
//        if (!ArchTaskExecutor.getInstance().isMainThread()) {
//            throw new IllegalStateException("Cannot invoke " + methodName + " on a background"
//                    + " thread");
//        }
    }
}
