package me.vinachiong.androidlib.webview.aidl

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.IBinder.DeathRecipient
import android.os.RemoteException
import me.vinachiong.androidlib.IBinderPool
import me.vinachiong.androidlib.webview.aidl.mainprocess.MainProAidlInterface
import me.vinachiong.androidlib.webview.aidl.mainprocess.MainProHandleRemoteService
import java.util.concurrent.CountDownLatch

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class RemoteWebBinderPool private constructor(private val mContext: Context) {
    
    private var mBinderPool: IBinderPool? = null
    private var mConnectBinderPoolCountDownLatch: CountDownLatch? =null
    
    // 监听IBinder Died 回调：进行释放 mBinderPool对象的处理
    private val mBinderPoolDeathRecipient: DeathRecipient = object : DeathRecipient {
        // 6
        override fun binderDied() {
            mBinderPool!!.asBinder().unlinkToDeath(this, 0)
            mBinderPool = null
            connectBinderPoolService()
        }
    }
    
    private val mBinderPoolConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (null != service && service is IBinderPool) {
                mBinderPool = IBinderPool.Stub.asInterface(service)
                try {
                    mBinderPool?.asBinder()?.linkToDeath(mBinderPoolDeathRecipient, 0)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
                mConnectBinderPoolCountDownLatch?.countDown()
            }
        }
    
        override fun onServiceDisconnected(name: ComponentName?) {
        
        }
    }
    
    
    @Synchronized private fun connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = CountDownLatch(1)
        val service = Intent(mContext, MainProHandleRemoteService::class.java)
        mContext.bindService(service, mBinderPoolConnection, Context.BIND_AUTO_CREATE)
        try {
            mConnectBinderPoolCountDownLatch?.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
    
    // AIDL接口：IBinderPool.aidl的服务端实现
    class RemoteBinderImpl(private val mContext: Context): IBinderPool.Stub() {
        override fun queryBinder(binderCode: Int): IBinder? {
            return when(binderCode) {
                BINDER_WEB_AIDL -> {
                    MainProAidlInterface(mContext)
                }
                else -> null
            }
        }
    }
    
    fun queryBinder(binderCode: Int): IBinder? {
        return try {
            mBinderPool?.queryBinder(binderCode)
        } catch (e: RemoteException) {
            e.printStackTrace()
            null
        }
    }
    
    companion object {
        const val BINDER_WEB_AIDL = 1
        
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var sInstance: RemoteWebBinderPool? = null
        
        @JvmStatic
        fun getInstance(context: Context): RemoteWebBinderPool = sInstance?: synchronized(this) {
            return sInstance?: RemoteWebBinderPool(context.applicationContext).also { sInstance = it }
        }
    }
    
    init {
        connectBinderPoolService()
    }
}