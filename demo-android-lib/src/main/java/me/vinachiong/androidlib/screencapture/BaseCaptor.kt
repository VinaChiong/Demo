package me.vinachiong.androidlib.screencapture

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import java.io.File


/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
abstract class BaseCaptor(
    protected val mOptions: CaptureOptions = CaptureOptions.DEFAULT,
    private val mHandler: CaptureHandler = CaptureHandler(mOptions)
) : ScreenCaptor {
    
    override fun capture(callback: CaptureResultCallback) {
     
        val bitmap = doCaptureBitmap()
    
        // TODO 提取截屏后的Bitmap信息，包括分辨率、大小等, 记录到日志或本地缓存
        mHandler.handler(bitmap, Callback(callback))
    }
    
    abstract fun doCaptureBitmap():Bitmap?
    
    private inner class Callback(val outerCallback: CaptureResultCallback) : CaptureHandler.HandleCallback {
        override fun onHandleSuccess(file: File) {
            outerCallback.onFinish(file)
        }
    
        override fun onHandleError(msg: String, e: Exception) {
            outerCallback.onError(msg)
        }
    }
}