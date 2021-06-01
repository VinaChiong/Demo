package me.vinachiong.androidlib.webview.interfaces

import android.content.Context
import android.webkit.WebResourceRequest
import android.webkit.WebView

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
interface CommonWebViewCallback {
    
    fun getCommandLevel(): Int
    
    fun pageStarted(url: String?)
    
    fun pageFinished(url: String?)
    
    fun overrideUrlLoading(view: WebView?, res: WebResourceRequest?): Boolean
    
    fun onError()
    
    fun exec(context: Context?, commandLevel: Int, cmd: String?, params: String?, webView: WebView?)
}