package me.vinachiong.androidlib.screencapture

import java.io.File

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
interface ScreenCaptor {
    fun capture(callback: CaptureResultCallback)
}

interface CaptureResultCallback {
    fun onFinish(file: File)
    
    fun onError(msg: String)
}