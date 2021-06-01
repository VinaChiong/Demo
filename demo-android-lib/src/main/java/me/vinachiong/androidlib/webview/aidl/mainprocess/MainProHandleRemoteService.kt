package me.vinachiong.androidlib.webview.aidl.mainprocess

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import me.vinachiong.androidlib.webview.aidl.RemoteWebBinderPool

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class MainProHandleRemoteService: Service() {
    
    private lateinit var mContext: Context
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
    
    
    override fun onBind(intent: Intent): IBinder? {
        val pid = android.os.Process.myPid()
        return RemoteWebBinderPool.RemoteBinderImpl(mContext)
    }
    
    
}