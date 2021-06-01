package me.vinachiong.androidlib.webview.command

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import com.google.gson.Gson
import me.vinachiong.androidlib.IWebAidlCallback
import me.vinachiong.androidlib.IWebAidlInterface
import me.vinachiong.androidlib.webview.SystemInfoUtil
import me.vinachiong.androidlib.webview.WebConstants
import me.vinachiong.androidlib.webview.aidl.RemoteWebBinderPool
import me.vinachiong.androidlib.webview.interfaces.Action
import me.vinachiong.androidlib.webview.interfaces.CommandResultCallback
import me.vinachiong.androidlib.webview.interfaces.DispatcherCallBack
import me.vinachiong.androidlib.webview.view.CommonWebView
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
object CommandDispatcher {
    
    private var iWebAidlInterface: IWebAidlInterface? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    
    /**
     * 先确保已获取iWebAidlInterface的实例
     * 无论当前是否在子进程，都通过AIDL来获取IWebAidlInterface的实例
     * 然后
     * @param context
     * @param action
     */
    fun initAidlConnect(context: Context, action: Action<Any?>?) {
        if (null != iWebAidlInterface) {
            action?.call(null)
            return
        }
        Thread {
            Log.i("AIDL", "Begin to connect with main process")
            val pool = RemoteWebBinderPool.getInstance(context)
            val binder = pool.queryBinder(RemoteWebBinderPool.BINDER_WEB_AIDL)
            iWebAidlInterface = IWebAidlInterface.Stub.asInterface(binder)
            Log.i("AIDL", "Connect success with main process")
            action?.call(null)
        }.start()
    }
    
    fun exec(
        context: Context?,
        commandLevel: Int,
        cmd: String?,
        params: String?,
        webView: WebView?,
        callback: DispatcherCallBack?
    ) {
        requireNotNull(context)
        
        
        // 区分是否属于UI command
        if (CommandManager.checkHitUICommand(commandLevel, cmd)) {
            execUi(context, commandLevel, cmd, params, webView, callback)
        } else {
        
        }
    }
    
    private val parameterizedType = object : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(String::class.java, Any::class.java)
        }
        
        override fun getRawType(): Type? {
            return Map::class.java
        }
        
        override fun getOwnerType(): Type? {
            return null
        }
    }
    private val gson = Gson()
    
    /**
     * UI级别的命令，哪怕在子进程，也可以执行处理
     */
    private fun execUi(context: Context, level: Int, cmd: String?,
                       params: String?, webView: WebView?, dispatcherCallBack: DispatcherCallBack?) {
        val mapParams: Map<String, Any> = gson.fromJson(params, parameterizedType)
        CommandManager.findAndExecUICommand(context, level, cmd, mapParams, object: CommandResultCallback {
            override fun onResult(status: Int, action: String?, result: Any?) {
                try {
                    if (status == WebConstants.CONTINUE) {
                        // 转发UI命令结果 到 其他非UI命令处理
                        execNonUi(context, level, action, gson.toJson(result), webView, dispatcherCallBack)
                    } else {
                        // 成功或失败，都回调到 JS前端
                        handleCallback(status, action, gson.toJson(result), webView, dispatcherCallBack)
                    }
                } catch (e: Exception) {
                
                }
            }
        })
    }
    
    /**
     * 非UI级别的命令，如果需要依赖主进程的用户相关数据
     */
    private fun execNonUi(context: Context, level: Int, cmd: String?, params: String, webView: WebView?, dispatcherCallBack: DispatcherCallBack?) {
        val pid = android.os.Process.myPid()
        if (SystemInfoUtil.inMainProcess(context, pid)) {
            CommandManager.findAndExecNonUiCommand(context, level, cmd, gson.fromJson(params, parameterizedType),
                   object: CommandResultCallback {
                           override fun onResult(status: Int, action: String?, result: Any?) {
                               handleCallback(status, action, gson.toJson(result), webView, dispatcherCallBack)
                           }
                       })
        } else {
            iWebAidlInterface?.handleWebAction(level, cmd, params, object: IWebAidlCallback.Stub() {
                override fun onResult(responseCode: Int, actionName: String?, response: String?) {
                    handleCallback(responseCode, actionName, response, webView, dispatcherCallBack)
                }
            })
        }
    }
    
    private fun handleCallback(responseCode: Int, actionName: String?, response: String?,
                               webView: WebView?, dispatcherCallBack: DispatcherCallBack?) {
        mainHandler.post {
            val responseMap = gson.fromJson<Map<String, Any>>(response, parameterizedType)
            
            // 回调给命令发送端前，先执行处理
            dispatcherCallBack?.preHandleBeforeCallback(responseCode, actionName, response)
            
            // Native端处理的回调结果中，含有NATIVE_2_WEB_CALLBACK，
            // 则需要让JS端加载执行
            responseMap[WebConstants.NATIVE_2_WEB_CALLBACK]?.also { jsCallbackName ->
                if (jsCallbackName.toString().isNotEmpty() && webView is CommonWebView) {
                    webView.handleCallback(response) // 执行JS脚本
                }
            }
        }
    }
}