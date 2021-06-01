package me.vinachiong.androidlib.webview.interfaces

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
interface DispatcherCallBack {
    
    fun preHandleBeforeCallback(responseCode: Int, actionName: String?, response: String?): Boolean
}