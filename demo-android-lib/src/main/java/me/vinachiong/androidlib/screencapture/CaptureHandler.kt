package me.vinachiong.androidlib.screencapture

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class CaptureHandler(
    private val mOptions: CaptureOptions, private val executor: ExecutorService = Executors.newSingleThreadExecutor()
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    
    fun handler(bitmap: Bitmap?, callback: HandleCallback) {
        // 直接保存本地、或仅保存临时文件
        val sb = StringBuilder()
        if (null != bitmap) {
            sb.append("bitmap is not empty\n").append("size = ${bitmap.byteCount}")
            val tempFileName = mOptions.getTempFile()
            if (null != tempFileName) {
                bitmap.save(tempFileName, callback)
            }
        } else {
        
        }
        Log.e("CaptureHandler", sb.toString())
        
        //        bitmap?.recycle()
    }
    
    private fun Bitmap.save(path: File, callback: HandleCallback) {
        // TODO 保证同一时间 只有一个Bitmap被保存为文件
        executor.execute {
            var bos: ByteArrayOutputStream? = null
            var fos: FileOutputStream? = null
            try {
                bos = ByteArrayOutputStream()
                val quality = 100
                val result: Boolean = this.compress(Bitmap.CompressFormat.JPEG, quality, bos)
                if (!result) {
                    val bitmap = this.copy(Bitmap.Config.ARGB_8888, true)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
                }
                
                mainHandler.post {
                    callback.onHandleSuccess(path)
                }
                fos = FileOutputStream(path)
                fos.write(bos.toByteArray(), 0, bos.toByteArray().size)
                fos.flush()
                
            } catch (e: IOException) {
                e.printStackTrace()
                mainHandler.post {
                    callback.onHandleError(e.message
                                                   ?: "error", e)
                }
            } finally {
                bos?.close()
                fos?.close()
            }
        }
    }
    
    interface HandleCallback {
        
        fun onHandleSuccess(file: File)
        
        fun onHandleError(msg: String, e: Exception)
    }
}