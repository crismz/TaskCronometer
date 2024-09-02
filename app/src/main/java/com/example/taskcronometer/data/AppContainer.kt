package com.example.taskcronometer.data

import android.content.Context


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val tasksRepository: TasksRepository
}

/**
 * [AppContainer] implementation that provided instance of [OfflineTasksRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [TasksRepository]
     */
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(TaskCronometerDatabase.getDatabase(context).taskDao())
    }
}