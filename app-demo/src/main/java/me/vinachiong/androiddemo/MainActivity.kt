package me.vinachiong.androiddemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.vinachiong.androiddemo.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, MainFragment.newInstance()).commitNow()
        }
    }
    
    override fun onBackPressed() {
        if (supportFragmentManager.fragments.size == 1) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStackImmediate()
        }
        val hh = arrayOf<ArrayList<String>>(ArrayList<String>(), ArrayList<String>())
    }
}