package com.example.yeticlicker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*

class AutoClickService : Service() {
    companion object {
        const val AUTO_CLICK_MS: String = "auto click ms"
        const val CURRENT_SCORE: String = "current score"
        const val MAX_SCORE: String = "max score"
        const val CLICK: String = "click"
    }
    private val timer: Timer = Timer()

    var gs: GameState = GameState()

    private val timerTask: TimerTask = object: TimerTask() {
        override fun run() {
            gs.currentScore += gs.click
            if(gs.currentScore >= gs.maxScore) {
                timer.cancel()
                timer.purge()
                stopService(Intent(this@AutoClickService, AutoClickService::class.java))
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = NotificationCompat.Builder(this, "com.example.clickerapp.DEFALUT")
            .setContentTitle("clicker is running")
            .setSmallIcon(R.drawable.image_to_click)

        val noti = builder.build()

        gs.autoCpsMS = intent!!.getLongExtra(AUTO_CLICK_MS, 0)
        gs.currentScore = intent!!.getIntExtra(CURRENT_SCORE, 0)
        gs.maxScore = intent!!.getIntExtra(MAX_SCORE, 0)
        gs.click = intent!!.getIntExtra(CLICK, 0)

        if (gs.autoCpsMS > 0) {
            timer.scheduleAtFixedRate(timerTask, 0, gs.autoCpsMS)
        }
        startForeground(1, noti)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        val intent: Intent = Intent("com.example.clickerapp.UPDATE")
        intent.putExtra(CURRENT_SCORE, gs.currentScore)
        sendBroadcast(intent)
        super.onDestroy()
    }
}
