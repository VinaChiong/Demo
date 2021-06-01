package me.vinachiong.androidlib.webview.command

import android.content.Context
import me.vinachiong.androidlib.webview.WebConstants
import me.vinachiong.androidlib.webview.interfaces.Command
import me.vinachiong.androidlib.webview.interfaces.CommandResultCallback

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
internal object CommandManager {
    private val uiDepCommandSet = UiDepCommandSet()
    
    fun registerCommand(level: Int, command: Command) {
        when (level) {
            WebConstants.LEVEL_UI -> uiDepCommandSet.registerCommand(command)
        }
    }
    
    fun checkHitUICommand(level: Int, action: String?): Boolean {
        if (action == null || action.isEmpty()) return false
        return uiDepCommandSet.getCommand(action) != null
    }
    
    fun findAndExecUICommand(context: Context?, level: Int, action: String?,
                             params: Map<String, Any>?, resultBack: CommandResultCallback?
    ) {
        if (action == null || action.isEmpty()) return
        uiDepCommandSet.getCommand(action)?.exec(context, params, resultBack);
    }
    
    fun findAndExecNonUiCommand(
        context: Context?, level: Int, action: String?,
        params: Map<String, Any>?, resultBack: CommandResultCallback?) {
    
    }
    
}