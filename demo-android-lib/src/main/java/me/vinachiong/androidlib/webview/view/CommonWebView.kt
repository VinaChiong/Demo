package me.vinachiong.androidlib.webview.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.MotionEvent
import android.webkit.*
import com.google.gson.Gson
import me.vinachiong.androidlib.webview.interfaces.CommonWebViewCallback
import me.vinachiong.androidlib.webview.interfaces.JsRemoteInterface

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
internal class CommonWebView(context: Context) : WebView(context) {
    
    private val mContext: Context = context
    private var mWebViewCallBack: CommonWebViewCallback? = null
    
    private val jsRemoteInterface: JsRemoteInterface = JsRemoteInterface(mContext)
    
    private var mTouchByUser = false
    private var isReady = false
    
    val isTouchByUser = mTouchByUser
    var mHeaders: Map<String, String>? = null
    
    init {
        // TODO setWebViewSetting
        
        webViewClient = MyWebViewClient()
        jsRemoteInterface.mAidlCommand = object: JsRemoteInterface.AidlCommand {
            override fun exec(context: Context, cmd: String, param: String) {
                mWebViewCallBack?.also {
                    it.exec(context, it.getCommandLevel(), cmd, param, this@CommonWebView)
                }
            }
        }
        setJavascriptInterface(jsRemoteInterface)
    }
    
    fun registerCommonWebViewCallback(callback: CommonWebViewCallback?) {
        this.mWebViewCallBack = callback
    }
    
    fun handleCallback(response: String?) {
        response?.also {
            if (it.isNotEmpty()) {
                val trigger = "javascript:dj.callback($response)"
                loadJS(trigger)
            }
        }
    }
    
    @SuppressLint("AddJavascriptInterface", "SetJavaScriptEnabled")
    fun setJavascriptInterface(obj: JsRemoteInterface) {
        addJavascriptInterface(obj, "android")
    }
    
    fun resetAllState() {
        mTouchByUser = false
    }
    
    /** 让JS端执行脚本*/
    fun loadJS(cmd: String, params: Any) {
        val triggerScript = "javascript:${cmd}(${Gson().toJson(params)})"
        loadJS(triggerScript)
    }
    
    /** 让JS端执行脚本*/
    fun loadJS(triggerScript: String) {
        if (triggerScript.isNotEmpty()) {
            evaluateJavascript(triggerScript, null)
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mTouchByUser = true
        }
        return super.onTouchEvent(event)
    }
    
    
//    fun dispatchEvent(params: Map<*, *>?) {
//        loadJS("dj.dispatchEvent", params)
//    }
    
    inner class MyWebViewClient: WebViewClient() {
    
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val reqUrl = request.url.toString()
            // 当前链接的重定向, 通过是否发生过点击行为来判断
            if (!isTouchByUser) {
                return super.shouldOverrideUrlLoading(view, request)
            }
            // 如果链接跟当前链接一样，表示刷新
            if (url == reqUrl) {
                return super.shouldOverrideUrlLoading(view, request)
            }
           
            if (handleLinkedWithScheme(reqUrl)) {
                return true
            }
            if (mWebViewCallBack?.overrideUrlLoading(view, request) == true) {
                return true
            }
            // 控制页面中点开新的链接在当前webView中打开
            if (null != mHeaders) {
                view.loadUrl(reqUrl, mHeaders!!)
            } else {
                view.loadUrl(reqUrl)
            }
            
            return super.shouldOverrideUrlLoading(view, request)
        }
    
        private fun handleLinkedWithScheme(url: String): Boolean {
            // SMS:// , ACTION://, TEL:
            return false
        }
    
        override fun onPageFinished(view: WebView?, url: String?) {
            if (null != url && url.isNotEmpty()) {
                isReady = true
            }
            mWebViewCallBack?.pageFinished(url)
        }
    
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            mWebViewCallBack?.pageStarted(url)
        }
    
        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            mWebViewCallBack?.onError()
        }
    
        override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
            // TODO toast SSL的错误信息，让用户选择是否 继续请求 还是 取消请求
            super.onReceivedSslError(view, handler, error)
        }
    }
}