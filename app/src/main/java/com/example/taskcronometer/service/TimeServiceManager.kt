package com.example.taskcronometer.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.taskcronometer.TaskCronometerApplication
import com.example.taskcronometer.data.Task
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object TimeServiceManager {
    private var timerService: TimerService? = null
    private var isBound = mutableStateOf(false)

    val connection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            isBound.value = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound.value = false
        }
    }

    fun getIsBound(): Boolean {
        return isBound.value
    }

    fun getRemainingTime(timerId: Int): Int {
        return timerService?.getRemainingTime(timerId) ?: 0
    }

    fun getTimerPaused(timerId: Int): Boolean? {
        return timerService?.getTimerPaused(timerId)
    }

    fun startTimer(timerId: Int, time: Int) {
        if (isBound.value) {
            Intent(TaskCronometerApplication.getAppContext(), TimerService::class.java).apply {
                putExtra("timerId", timerId)
                putExtra("time", time)
                putExtra("action", TimeServiceActions.START.action)
            }.also { intent ->
                TaskCronometerApplication.getAppContext().startService(intent)
            }
        }
    }

    fun pauseTimer(timerId: Int) {
        if (isBound.value) {
            Intent(TaskCronometerApplication.getAppContext(), TimerService::class.java).apply {
                putExtra("timerId", timerId)
                putExtra("action", TimeServiceActions.PAUSE.action)
            }.also { intent ->
                TaskCronometerApplication.getAppContext().startService(intent)
            }
        }
    }

    fun resumeTimer(timerId: Int) {
        if (isBound.value) {
            Intent(TaskCronometerApplication.getAppContext(), TimerService::class.java).apply {
                putExtra("timerId", timerId)
                putExtra("action", TimeServiceActions.RESUME.action)
            }.also { intent ->
                TaskCronometerApplication.getAppContext().startService(intent)
            }
        }
    }

}