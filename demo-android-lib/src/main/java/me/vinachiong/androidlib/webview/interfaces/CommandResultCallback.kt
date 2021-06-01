package me.vinachiong.androidlib.webview.interfaces

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
interface CommandResultCallback {
    fun onResult(status: Int, action: String?, result: Any?)
}