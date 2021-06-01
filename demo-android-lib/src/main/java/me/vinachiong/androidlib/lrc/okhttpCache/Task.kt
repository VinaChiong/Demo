package me.vinachiong.androidlib.lrc.okhttpCache

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
abstract class Task(
    val name: String,
    val cancelable: Boolean = true
) {
    // Guarded by the TaskRunner.
    internal var queue: TaskQueue? = null
    
    /** Undefined unless this is in [TaskQueue.futureTasks]. */
    internal var nextExecuteNanoTime = -1L
    
    /** Returns the delay in nanoseconds until the next execution, or -1L to not reschedule. */
    abstract fun runOnce(): Long
    
    internal fun initQueue(queue: TaskQueue) {
        if (this.queue === queue) return
        
        check(this.queue === null) { "task is in multiple queues" }
        this.queue = queue
    }
    
    override fun toString() = name
}