package me.vinachiong.androiddemo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import me.vinachiong.androiddemo.R
import me.vinachiong.android.activity.launchmode.StandardAct
import me.vinachiong.androiddemo.ui.screencaptor.ScreenCaptorFragment

class MainFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance() = MainFragment()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<TextView>(R.id.btn_launch_mode).setOnClickListener(this)
        view.findViewById<TextView>(R.id.btn_bubble_view).setOnClickListener(this)
        view.findViewById<TextView>(R.id.btn_screen_captor).setOnClickListener(this)
    }
    
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_launch_mode   -> {
                startActivity(Intent(v.context, StandardAct::class.java))
            }
            R.id.btn_bubble_view   -> {
//                activity?.supportFragmentManager?.also {
//                    it.beginTransaction().apply {
//                        replace(R.id.container, BubbleViewFragment())
//                        addToBackStack(null)
//                    }.commit()
//                }
                handleReplaceFragmentToBackStack(BubbleViewFragment::class.java)
            }
            R.id.btn_screen_captor -> { //                activity?.supportFragmentManager?.also {
                //                    it.beginTransaction().apply {
                //                        replace(R.id.container, ScreenCaptorFragment())
                //                        addToBackStack(null)
                //                    }.commit()
                //                }
                handleReplaceFragmentToBackStack(ScreenCaptorFragment::class.java)
            }
        }
    }
    
    private fun <T : Fragment> handleReplaceFragmentToBackStack(fragmentClass: Class<T>) {
        activity?.supportFragmentManager?.also {
            it.beginTransaction().apply {
                replace(R.id.container, fragmentClass.newInstance())
                addToBackStack(null)
            }.commit()
        }
    }
}