package me.vinachiong.androidlib.webview.interfaces

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
interface Action<T> {
    fun call(t: T)
}