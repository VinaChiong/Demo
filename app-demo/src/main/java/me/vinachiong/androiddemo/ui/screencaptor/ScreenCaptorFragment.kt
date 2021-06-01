package me.vinachiong.androiddemo.ui.screencaptor

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import me.vinachiong.androidlib.screencapture.CaptureOptions
import me.vinachiong.androidlib.screencapture.CaptureResultCallback
import me.vinachiong.androidlib.screencapture.DecorViewCaptor
import me.vinachiong.androidlib.screencapture.ScreenCaptor
import java.io.File

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class ScreenCaptorFragment() : Fragment() {
    
    private lateinit var mScreenCaptor: ScreenCaptor
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val o = CaptureOptions.Builder().tempDir(context.cacheDir.absolutePath).build()
        mScreenCaptor = DecorViewCaptor(options = o)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        
        return RelativeLayout(inflater.context).apply {
            layoutParams =
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                            RelativeLayout.LayoutParams.MATCH_PARENT)
            
            gravity = Gravity.CENTER
            val btn = Button(inflater.context)
            btn.text = "截图"
            btn.setOnClickListener {
                
                mScreenCaptor.capture(object: CaptureResultCallback {
                    override fun onFinish(file: File) {
                        Toast.makeText(inflater.context, file.absolutePath, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(msg: String) {
                        Toast.makeText(inflater.context, "error: $msg", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            addView(btn)
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.apply {
            (mScreenCaptor as DecorViewCaptor).setDecorView(decorView)
        }
    }
}
