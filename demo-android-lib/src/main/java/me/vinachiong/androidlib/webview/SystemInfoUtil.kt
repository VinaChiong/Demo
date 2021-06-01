package me.vinachiong.androidlib.webview

import android.app.ActivityManager
import android.content.Context

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */

object SystemInfoUtil {
    
    @JvmStatic
    fun inMainProcess(context: Context, pid:Int): Boolean {
        val packageName = context.packageName
        val processName = getProcessNameByPid(context, pid)
        return packageName == processName
    }
    
    @JvmStatic
    fun getProcessNameByPid(context: Context,pid: Int) : String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val process = am.runningAppProcesses?.find {
            it.pid == pid
        }
        return process?.processName?: ""
    }
}