package me.vinachiong.androidlib.webview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import me.vinachiong.androidlib.R
import me.vinachiong.androidlib.webview.command.CommandDispatcher
import me.vinachiong.androidlib.webview.interfaces.CommonWebViewCallback
import me.vinachiong.androidlib.webview.interfaces.DispatcherCallBack
import me.vinachiong.androidlib.webview.view.CommonWebView

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
abstract class BaseWebViewFragment: Fragment(), CommonWebViewCallback {
    
    private lateinit var mWebView: CommonWebView
    
    @LayoutRes
    abstract fun getLayoutRes(): Int
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(getLayoutRes(), container, false)
        mWebView = view.findViewById(R.id.webView)
        return view
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mWebView.registerCommonWebViewCallback(this) // 监听WebView的回调
        
        CommandDispatcher
    }
    
    override fun getCommandLevel(): Int {
        return WebConstants.LEVEL_BASE
    }
    
    override fun exec(context: Context?, commandLevel: Int, cmd: String?, params: String?, webView: WebView?) {
        // 接收来自JS端的命令
        CommandDispatcher.exec(context, commandLevel, cmd, params, webView, getDispatcherCallBack())
    }
    
    protected fun getDispatcherCallBack(): DispatcherCallBack? {
        return null
    }
}