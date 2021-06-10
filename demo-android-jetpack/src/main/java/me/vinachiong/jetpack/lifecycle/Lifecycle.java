package me.vinachiong.jetpack.lifecycle;

/**
 * 【手动仿写Lifecycle系列】
 * '生命周期'的抽象类。从Activity中提出具体的类型。
 * 通过接收 LifecycleOwner 发送的 Event 进行感知，并更新自己的状态。
 * 同时允许外部
 *
 * @author vina.chiong
 * @version v1.0.0
 */
public abstract class Lifecycle {
//    /**
//     * Lifecycle 协程扩展 用于stashes CoroutineScope
//     *
//     * @hide used by lifecycle-common-ktx
//     */
//    AtomicReference<Object> mInternalScopeRef = new AtomicReference<>();

    /**
     *
     * 接收注册「LC事件的观察者」。并且只有在Owner状态活跃时候，才会分发事件
     *
     * @param observer The observer to notify.
     */
    public abstract void addObserver(LifecycleObserver observer);

    /**
     * 尝试移除具体的「LC事件的观察者」，并解除相关引用，避免内存泄漏
     *
     * @param observer The observer to be removed.
     */
    public abstract void removeObserver(LifecycleObserver observer);

    /**
     * 当前 LC 的状态
     *
     * @return The current state of the Lifecycle.
     */
    public abstract State getCurrentState();

    /**
     * LC 的事件枚举类
     * 组件内流通的数据。
     * 是观察者模式得以实现的重要基础
     *
     */
    public enum Event {
        /**
         * Constant for onCreate event of the {@link LifecycleOwner}.
         */
        ON_CREATE,
        /**
         * Constant for onStart event of the {@link LifecycleOwner}.
         */
        ON_START,
        /**
         * Constant for onResume event of the {@link LifecycleOwner}.
         */
        ON_RESUME,
        /**
         * Constant for onPause event of the {@link LifecycleOwner}.
         */
        ON_PAUSE,
        /**
         * Constant for onStop event of the {@link LifecycleOwner}.
         */
        ON_STOP,
        /**
         * Constant for onDestroy event of the {@link LifecycleOwner}.
         */
        ON_DESTROY,
        /**
         * An {@link Event Event} constant that can be used to match all events.
         */
        ON_ANY;

        /**
         * Returns the {@link Lifecycle.Event} that will be reported by a {@link Lifecycle}
         * leaving the specified {@link Lifecycle.State} to a lower state, or {@code null}
         * if there is no valid event that can move down from the given state.
         *
         * @param state the higher state that the returned event will transition down from
         * @return the event moving down the lifecycle phases from state
         */
        public static Event downFrom(State state) {
            switch (state) {
                case CREATED:
                    return ON_DESTROY;
                case STARTED:
                    return ON_STOP;
                case RESUMED:
                    return ON_PAUSE;
                default:
                    return null;
            }
        }

        /**
         * Returns the {@link Lifecycle.Event} that will be reported by a {@link Lifecycle}
         * entering the specified {@link Lifecycle.State} from a higher state, or {@code null}
         * if there is no valid event that can move down to the given state.
         *
         * @param state the lower state that the returned event will transition down to
         * @return the event moving down the lifecycle phases to state
         */
        public static Event downTo(State state) {
            switch (state) {
                case DESTROYED:
                    return ON_DESTROY;
                case CREATED:
                    return ON_STOP;
                case STARTED:
                    return ON_PAUSE;
                default:
                    return null;
            }
        }

        /**
         * Returns the {@link Lifecycle.Event} that will be reported by a {@link Lifecycle}
         * leaving the specified {@link Lifecycle.State} to a higher state, or {@code null}
         * if there is no valid event that can move up from the given state.
         *
         * @param state the lower state that the returned event will transition up from
         * @return the event moving up the lifecycle phases from state
         */

        public static Event upFrom(State state) {
            switch (state) {
                case INITIALIZED:
                    return ON_CREATE;
                case CREATED:
                    return ON_START;
                case STARTED:
                    return ON_RESUME;
                default:
                    return null;
            }
        }

        /**
         * Returns the {@link Lifecycle.Event} that will be reported by a {@link Lifecycle}
         * entering the specified {@link Lifecycle.State} from a lower state, or {@code null}
         * if there is no valid event that can move up to the given state.
         *
         * @param state the higher state that the returned event will transition up to
         * @return the event moving up the lifecycle phases to state
         */

        public static Event upTo(State state) {
            switch (state) {
                case CREATED:
                    return ON_CREATE;
                case STARTED:
                    return ON_START;
                case RESUMED:
                    return ON_RESUME;
                default:
                    return null;
            }
        }

        public State getTargetState() {
            switch (this) {
                case ON_CREATE:
                case ON_STOP:
                    return State.CREATED;
                case ON_START:
                case ON_PAUSE:
                    return State.STARTED;
                case ON_RESUME:
                    return State.RESUMED;
                case ON_DESTROY:
                    return State.DESTROYED;
                case ON_ANY:
                    break;
            }
            throw new IllegalArgumentException(this + " has no target state");
        }
    }

    /**
     * Lifecycle states. You can consider the states as the nodes in a graph and
     * {@link Event}s as the edges between these nodes.
     */
    @SuppressWarnings("WeakerAccess")
    public enum State {
        DESTROYED,
        INITIALIZED,
        CREATED,
        STARTED,
        RESUMED;

        /**
         * 根据枚举类中的 ordinal数值进行大小比较。
         *
         * @param state State to compare with
         * @return true if this State is greater or equal to the given {@code state}
         */
        public boolean isAtLeast(State state) {
            return compareTo(state) >= 0;
        }
    }
}
