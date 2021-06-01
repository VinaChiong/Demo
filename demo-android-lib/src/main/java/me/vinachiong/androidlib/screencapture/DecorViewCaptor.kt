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
class DecorViewCaptor(
    options: CaptureOptions
) : BaseCaptor(options) {
    
    private var mDecorView: View? =null
    
    fun setDecorView(decorView: View) {
        mDecorView = decorView
    }
    
    override fun doCaptureBitmap(): Bitmap? {
        if (null == mDecorView) return null
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // 方式一:
            mDecorView!!.run {
                isDrawingCacheEnabled = true
                buildDrawingCache()
                drawingCache
            }
        } else {
            Bitmap.createBitmap(mDecorView!!.width, mDecorView!!.height, mOptions.bitmapConfig).apply {
                val canvas = Canvas()
                canvas.setBitmap(this)
                mDecorView!!.draw(canvas) // 把decorView，通过Canvas写到新创建的Bitmap对象
            }
        }
    }
}