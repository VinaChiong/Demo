package me.vinachiong.androidlib.jetpack.lifecycle;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author vina.chiong
 * @version v1.0.0
 */
public abstract class Lifecycle {
    /**
     * Lifecycle coroutines extensions stashes the CoroutineScope into this field.
     *
     * @hide used by lifecycle-common-ktx
     */
    AtomicReference<Object> mInternalScopeRef = new AtomicReference<>();

    /**
     * Adds a LifecycleObserver that will be notified when the LifecycleOwner changes
     * state.
     * <p>
     * The given observer will be brought to the current state of the LifecycleOwner.
     * For example, if the LifecycleOwner is in {@link State#STARTED} state, the given observer
     * will receive {@link Event#ON_CREATE}, {@link Event#ON_START} events.
     *
     * @param observer The observer to notify.
     */
    public abstract void addObserver(LifecycleObserver observer);

    /**
     * Removes the given observer from the observers list.
     * <p>
     * If this method is called while a state change is being dispatched,
     * <ul>
     * <li>If the given observer has not yet received that event, it will not receive it.
     * <li>If the given observer has more than 1 method that observes the currently dispatched
     * event and at least one of them received the event, all of them will receive the event and
     * the removal will happen afterwards.
     * </ul>
     *
     * @param observer The observer to be removed.
     */
    public abstract void removeObserver(LifecycleObserver observer);

    /**
     * Returns the current state of the Lifecycle.
     *
     * @return The current state of the Lifecycle.
     */
    public abstract State getCurrentState();

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
         * Compares if this State is greater or equal to the given {@code state}.
         *
         * @param state State to compare with
         * @return true if this State is greater or equal to the given {@code state}
         */
        public boolean isAtLeast(State state) {
            return compareTo(state) >= 0;
        }
    }
}
