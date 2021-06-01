package me.vinachiong.androidlib.webview.interfaces

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class JsRemoteInterface(private val mContext: Context) {
    
    private val mHandler = Handler(Looper.getMainLooper())
    var mAidlCommand: AidlCommand? = null
    
    /**
     * 接收来自JS端调用 call(cmd, param)
     */
    @JavascriptInterface
    fun call(cmd: String, params: String) {
        mHandler.post {
            mAidlCommand?.exec(mContext, cmd, params)
        }
    }
    
    interface AidlCommand {
        fun exec(context: Context, cmd: String, param: String)
    }
}