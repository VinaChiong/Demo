package me.vinachiong.androidlib.webview.interfaces

import android.content.Context

/**
 *
 * d
 * @author vina.chiong
 * @version v1.0.0
 */
interface Command {
    
    val name: String
    
    fun exec(context: Context?, params: Map<String, Any>?, resultCallback: CommandResultCallback?)
}