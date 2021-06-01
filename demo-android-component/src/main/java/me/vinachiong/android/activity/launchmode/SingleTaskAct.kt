package me.vinachiong.android.activity.launchmode

import android.content.Intent
import android.util.Log

/**
 *
 *
 * @author vina.chiong
 * @version v1.0.0
 */
class SingleTaskAct : LauncherModeAct() {
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.i("SingleTaskAct", "SingleTask: onNewIntent")
    }
}