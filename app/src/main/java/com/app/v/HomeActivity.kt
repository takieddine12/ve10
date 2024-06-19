package com.app.v

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random


class HomeActivity : AppCompatActivity() {

    private lateinit var bubbleEmitterView: BubbleEmitterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val lockScreen  = findViewById<ImageView>(R.id.lock)
        val rippleBackground1 = findViewById<WaveView>(R.id.content1)
        val rippleBackground2 = findViewById<WaveView>(R.id.content2)
        val rippleBackground3 = findViewById<WaveView>(R.id.content3)
        val rippleBackground4 = findViewById<WaveView>(R.id.content4)
        val rippleBackground5 = findViewById<WaveView>(R.id.content5)
        val rippleBackground6 = findViewById<WaveView>(R.id.content6)
        rippleBackground1.startRippleAnimation()
        rippleBackground2.startRippleAnimation()
        rippleBackground3.startRippleAnimation()
        rippleBackground4.startRippleAnimation()
        rippleBackground5.startRippleAnimation()
        rippleBackground6.startRippleAnimation()

        bubbleEmitterView = findViewById(R.id.bubbleEmitter)
        emitBubbles()

        rippleBackground1.setOnClickListener {
            try {
                val intent = packageManager.getLaunchIntentForPackage("com.samsung.android.dialer")
                startActivity(intent)
            } catch (ex : Exception){
                Log.d("TAG","No app found with package name")
            }
        }
        rippleBackground2.setOnClickListener {
            try {
                val intent = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                startActivity(intent)
            } catch (ex : Exception){
                Log.d("TAG","No app found with package name")
            }
        }
        lockScreen.setOnClickListener {
            Intent(this@HomeActivity,AppActivity::class.java).apply {
                startActivity(this)
            }
        }
        startLockTask()


    }

    override fun onStart() {
        super.onStart()
        Utils.initAlarmManager(this)
        Utils.cancelAlarManager()
    }
    private fun isAirPlaneModeOn()  {
        val intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")

        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val isAirplaneModeOn = Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0
                if (isAirplaneModeOn) {
                    Intent(this@HomeActivity,AppActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }
        }

        registerReceiver(receiver, intentFilter)
    }
    private fun emitBubbles() {
        Handler().postDelayed({
            val size = Random.nextInt(20, 80)
            bubbleEmitterView.emitBubble(size)
            emitBubbles()
        }, Random.nextLong(100, 500))
    }
}