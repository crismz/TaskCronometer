package com.example.taskcronometer

import android.app.Application
import android.content.Context
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


    companion object {
        private lateinit var instance: TaskCronometerApplication

        fun getAppContext(): Context = instance.applicationContext
    }

    init {
        instance = this
    }

}