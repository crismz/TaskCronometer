package com.example.taskcronometer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.core.app.NotificationCompat
import com.example.taskcronometer.R
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class TimerService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var timers = mutableMapOf<Int, Int>() // Map of timerId to remaining time

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        val timerId = intent?.getIntExtra("timerId", 0)
        val time = intent?.getIntExtra("time", 0)
        timerId?.let {
            startTimer(it, time ?: 0)
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "TimerServiceChannel"
        val channelName = "Timer Service"
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Timer Service")
            .setContentText("Timers are running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(1, notification)
    }

    private fun startTimer(timerId: Int, time: Int) {
        timers[timerId] = time
        coroutineScope.launch {
            while (timers[timerId]!! > 0) {
                delay(1000L)
                timers[timerId] = timers[timerId]!! - 1
                // Update notification or broadcast remaining time to Activity
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}