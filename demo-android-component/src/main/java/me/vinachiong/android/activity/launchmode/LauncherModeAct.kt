package me.vinachiong.android.activity.launchmode

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import me.vinachiong.android.R

/**
 * LaunchMode 基类Activity
 *
 * @author vina.chiong
 * @version v1.0.0
 */
abstract class LauncherModeAct : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(LauncherModeCounter)
        
        setContentView(R.layout.act_launcher_mode)
    
        findViewById<Button>(R.id.btn_standard).setOnClickListener {
            startActivity(Intent(this, StandardAct::class.java))
        }
        findViewById<Button>(R.id.btn_single_top).setOnClickListener {
            startActivity(Intent(this, SingleTopAct::class.java))
        }
        findViewById<Button>(R.id.btn_single_task).setOnClickListener {
            startActivity(Intent(this, SingleTaskAct::class.java))
        }
        findViewById<Button>(R.id.btn_single_instance).setOnClickListener {
            startActivity(Intent(this, SingleInstanceAct::class.java))
        }
    
        findViewById<Button>(R.id.btn_standard_new_task).setOnClickListener {
            startActivity(Intent(this, StandardAct::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        findViewById<Button>(R.id.btn_single_top_new_task).setOnClickListener {
            startActivity(Intent(this, SingleTopAct::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        findViewById<Button>(R.id.btn_single_task_new_task).setOnClickListener {
            startActivity(Intent(this, SingleTaskAct::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
        findViewById<Button>(R.id.btn_single_instance_new_task).setOnClickListener {
            startActivity(Intent(this, SingleInstanceAct::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }
    
    override fun onStart() {
        super.onStart()
        findViewById<TextView>(R.id.task_info).apply {
            text = LauncherModeCounter.showTaskInfo(this@LauncherModeAct)
        }
    }
}