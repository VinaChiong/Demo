package me.vinachiong.androidlib.screencapture

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import java.lang.reflect.Method
import kotlin.math.abs

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class SysSurfaceControlCaptor(): BaseCaptor() {
    
    
    override fun doCaptureBitmap(): Bitmap? {
        return null
    }
    
//    private fun takeSystemScreenShot(): Bitmap? {
//        val mDisplayMetrics = Resources.getSystem().displayMetrics
//        var bmp: Bitmap? = null
//        val dims = floatArrayOf(mDisplayMetrics.widthPixels.toFloat(), mDisplayMetrics.heightPixels.toFloat())
//        val degrees: Float = getDegreesForRotation(mDisplay.getRotation())
//        val requiresRotation = degrees > 0
//        if (requiresRotation) {
//            mDisplayMatrix.reset()
//            mDisplayMatrix.preRotate(-degrees)
//            mDisplayMatrix.mapPoints(dims)
//            dims[0] = abs(dims[0])
//            dims[1] = abs(dims[1])
//        }
//        return try {
//            val demo = Class.forName("android.view.SurfaceControl")
//            val method: Method = demo.getMethod("screenshot", Integer.TYPE, Integer.TYPE)
//            bmp = method.invoke(demo, arrayOf<Int>(dims[0].toInt(), dims[1].toInt())) as Bitmap?
//            if (bmp == null) {
//                return null
//            }
//            if (requiresRotation) {
//
//                val ss = Bitmap.createBitmap(displayMetrics.widthPixels, displayMetrics, Bitmap.Config.RGB_565)
//                val c = Canvas(ss)
//                c.translate((ss.width / 2).toFloat(), (ss.height / 2).toFloat())
//                c.rotate(degrees)
//                c.translate(-dims[0] / 2, -dims[1] / 2)
//                c.drawBitmap(bmp, 0, 0, null)
//                c.setBitmap(null)
//                bmp.recycle()
//                bmp = ss
//            }
//            if (bmp == null) {
//                return null
//            }
//            bmp.setHasAlpha(false)
//            bmp.prepareToDraw()
//            bmp
//        } catch (e: Exception) {
//            e.printStackTrace()
//            bmp
//        }
//    }
    
    
}