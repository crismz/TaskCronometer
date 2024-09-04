package com.example.taskcronometer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.taskcronometer.R
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds

class TimerService : Service() {

    /**
     * Binder object used to bind the service to an activity
      */
    private val binder = LocalBinder()

    /**
     * Coroutine scope for running timers on a background thread
      */
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /**
     * Map to hold timer data (timerId -> remaining time in seconds)
      */
    private var timers = mutableMapOf<Int, Int>()

    // Tracks if a timer is paused
    private val pausedTimers = mutableMapOf<Int, Boolean>()

    /**
     *  LocalBinder class that provides a reference to the service
      */
    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    /**
     * Method is called when an activity binds to the service
      */
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    /**
     * This method is called when the service is started
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timerId = intent?.getIntExtra("timerId", 0)
        val time = intent?.getIntExtra("time", 0)
        val action = intent?.getStringExtra("action")

        timerId?.let {
            when (action) {
                TimeServiceActions.START.action -> {
                    startForegroundService(it)
                    startTimer(it, time ?: 0)
                }
                TimeServiceActions.PAUSE.action -> pauseTimer(it)
                TimeServiceActions.RESUME.action -> resumeTimer(it)
            }

        }

        return START_STICKY
    }


    /**
     * Method to start the service in the foreground with a persistent notification
      */
    private fun startForegroundService(notificationId: Int) {
        val channelId = "TimerServiceChannel_$notificationId"
        val channelName = "Timer Service $notificationId"
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Timer Service")
            .setContentText("Timers are running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                0
            }
        )
    }

    /**
     * Stops the foreground service and removes the notification.
     * Can be called from inside or outside the service.
     */
    fun stopForegroundService() {
        stopSelf()
    }

    private fun startTimer(timerId: Int, time: Int) {
        timers[timerId] = time
        pausedTimers[timerId] = false
        coroutineScope.launch {
            while (timers[timerId]!! > 0 && pausedTimers[timerId] == false) {
                // Wait for 1 second and decrease the time by 1 second
                delay(1000L)
                timers[timerId] = timers[timerId]!! - 1
                // Update notification or broadcast remaining time to Activity
            }
            //TODO("See if add a toast message or an alarm when finish")
        }
    }

    private fun pauseTimer(timerId: Int) {
        pausedTimers[timerId] = true
    }

    private fun resumeTimer(timerId: Int) {
        pausedTimers[timerId] = false
        startTimer(timerId, timers[timerId]!!)
    }

    fun getRemainingTime(timerId: Int): Int {
        return timers[timerId] ?: 0
    }

    fun getTimerPaused(timerId: Int): Boolean? {
        return pausedTimers[timerId]
    }

    override fun onCreate() {
        super.onCreate()
        //Toast.makeText(this, "Foreground Service created", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        //Toast.makeText(this, "Foreground Service destroyed", Toast.LENGTH_SHORT).show()
    }
}