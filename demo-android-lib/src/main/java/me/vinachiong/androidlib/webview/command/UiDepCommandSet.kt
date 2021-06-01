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
internal class UiDepCommandSet: CommandSet() {
    override val level: Int = WebConstants.LEVEL_UI
    
    init {
        registerCommand(Toast())
    }
    
    private inner class Toast: Command {
        override val name: String
            get() = "UI:Toast"
     
        override fun exec(context: Context?, params: Map<String, Any>?, resultCallback: CommandResultCallback?) {
        
        }
    }
}