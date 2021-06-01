package me.vinachiong.androidlib.webview.aidl.mainprocess

import android.content.Context
import com.google.gson.Gson
import me.vinachiong.androidlib.IWebAidlCallback
import me.vinachiong.androidlib.IWebAidlInterface
import me.vinachiong.androidlib.webview.command.CommandManager
import me.vinachiong.androidlib.webview.interfaces.CommandResultCallback
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.jvm.Throws

/**
 * 主进程实现的 IWebAidlInterface.aidl 接口的服务端
 * 处理来自 子进程WebView中JS发送的结构化消息
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class MainProAidlInterface(private val context: Context): IWebAidlInterface.Stub() {
    
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
    
    override fun handleWebAction(level: Int, actionName: String?, jsonParams: String?, callback: IWebAidlCallback?) {
        try {
            handleRemoteAction(level, actionName, Gson().fromJson(jsonParams, parameterizedType), callback)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    @Throws(Exception::class)
    private fun handleRemoteAction(level: Int, actionName:String?, jsonParams:String?,
                                   callback: IWebAidlCallback?) {
        val params: Map<String, Any> = Gson().fromJson(jsonParams, parameterizedType)
        CommandManager.findAndExecNonUiCommand(context, level, actionName, params, object: CommandResultCallback {
            override fun onResult(status: Int, action: String?, result: Any?) {
                callback?.onResult(status, actionName, jsonParams)
            }
        })
    }
}