package me.vinachiong.androidlib.webview

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
object WebConstants {
    const val LEVEL_UI = 0 // UI Command
    const val LEVEL_BASE = 1 // 基础level
    const val LEVEL_ACCOUNT = 2 // 涉及到账号相关的level
    
    const val CONTINUE = 2 // 继续分发command
    const val SUCCESS = 1 // 成功
    const val FAILED = 0 // 失败
    
    
    const val WEB_2_NATIVE_CALLBACK = "callback"
    const val NATIVE_2_WEB_CALLBACK = "callbackName"
}