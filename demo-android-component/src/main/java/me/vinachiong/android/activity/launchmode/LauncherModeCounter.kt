package me.vinachiong.android.activity.launchmode

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
object LauncherModeCounter : LifecycleEventObserver {
    
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                onCreate(source)
            }
        }
    }
    
    private fun onCreate(source: LifecycleOwner) {
        when  {
            source is StandardAct -> {}
            source is SingleInstanceAct -> { }
            source is SingleTopAct -> {}
            source is SingleTaskAct -> {}
        }
    }
    
    fun showTaskInfo(context: Context):String  {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val sb = StringBuffer()
        am.appTasks.forEach {
            it.taskInfo.apply {
                sb.append("""
                    | ============================================================
                    | Stack#${this.taskId} - Count = ${this.numActivities}
                    | TOP: ${this.topActivity?.shortClassName}
                    | BASE: ${this.baseActivity?.shortClassName}
                    | ============================================================
                    """.trimIndent())
            }
            
            sb.append("\n")
        }
        return sb.toString()
    }
}