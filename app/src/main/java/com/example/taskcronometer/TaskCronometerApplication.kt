package com.example.taskcronometer

import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.taskcronometer.data.AppContainer
import com.example.taskcronometer.data.AppDataContainer
import com.example.taskcronometer.service.TimeServiceManager
import com.example.taskcronometer.service.TimerService


class TaskCronometerApplication: Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        bindTimerService()
        container = AppDataContainer(this)
    }

    private fun bindTimerService() {
        Intent(this, TimerService::class.java).also { intent ->
            bindService(intent, TimeServiceManager.connection, Context.BIND_AUTO_CREATE)
        }
    }

    companion object {
        private lateinit var instance: TaskCronometerApplication

        fun getAppContext(): Context = instance.applicationContext
    }

    init {
        instance = this
    }

}