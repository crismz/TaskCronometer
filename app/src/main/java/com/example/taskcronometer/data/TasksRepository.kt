package com.example.taskcronometer.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface TasksRepository {
    /**
     * Retrieve all the tasks from the the given data source.
     */
    fun getAllTasksStream(): Flow<List<Task>>

    /**
     * Retrieve an task from the given data source that matches with the [id].
     */
    fun getTaskStream(id: Int): Flow<Task?>

    /**
     * Insert task in the data source
     */
    suspend fun insertTask(task: Task)

    /**
     * Delete task from the data source
     */
    suspend fun deleteTask(task: Task)

    /**
     * Update task in the data source
     */
    suspend fun updateTask(task: Task)
}