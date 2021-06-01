package me.vinachiong.androidlib.screencapture

import android.graphics.Bitmap
import java.io.File

/**
 * 截屏的相关参数配置
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class CaptureOptions (
    builder: Builder
) {
    val bitmapConfig: Bitmap.Config = builder.bitmapConfig
    val outputPath: String = builder.outputPath
    private val tempDir: String = builder.tempDir
    
    
    fun getTempFile(): File? {
        if (tempDir.isEmpty()) return null
        
        return File(tempDir, "SCR_CAP_TEMP.jpg")
    }
    
    class Builder {
        internal var bitmapConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
        internal var outputPath: String = ""
        internal var tempDir: String = ""
        
        fun tempDir(dir: String): Builder = apply {
            tempDir = dir
        }
        
        fun build(): CaptureOptions {
            return CaptureOptions(this)
        }
    }
    
    companion object {
        val DEFAULT = CaptureOptions(Builder())
    }
}