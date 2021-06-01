package me.vinachiong.androiddemo.ui.screencaptor

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import me.vinachiong.androidlib.screencapture.CaptureOptions
import me.vinachiong.androidlib.screencapture.DecorViewCaptor
import me.vinachiong.androidlib.screencapture.ScreenCaptor

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class ScreenCaptorObserver(
    private val options: CaptureOptions
): LifecycleEventObserver {
    
    lateinit var mScreenCaptor: ScreenCaptor
        private set
    
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START ->{
                if (!::mScreenCaptor.isInitialized) {
                    mScreenCaptor = DecorViewCaptor(options = options)
                }
            }
        }
    
    }
}
