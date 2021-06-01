package me.vinachiong.androiddemo.ui.main

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import me.vinachiong.androidlib.view.BubbleView

/**
 * 展示 BubbleView
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class BubbleViewFragment: Fragment() {
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    
        return RelativeLayout(inflater.context).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                       RelativeLayout.LayoutParams.MATCH_PARENT)
            
            gravity = Gravity.CENTER
            addView(BubbleView(inflater.context))
        }
    }
}