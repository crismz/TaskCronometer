package com.example.taskcronometer

import android.app.Application
import com.example.taskcronometer.data.AppContainer
import com.example.taskcronometer.data.AppDataContainer

class TaskCronometerApplication: Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}