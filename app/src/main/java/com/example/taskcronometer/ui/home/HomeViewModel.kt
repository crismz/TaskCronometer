package com.example.taskcronometer.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.taskcronometer.TaskCronometerApplication
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.data.TasksRepository
import com.example.taskcronometer.notification.NotificationWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to ...
 */
class HomeViewModel(
    private val tasksRepository: TasksRepository
): ViewModel() {
    /**
     * Holds home ui state. The list of tasks are retrieved from [TasksRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        tasksRepository.getAllTasksStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )


    fun startTimerTask(task: Task) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(workDataOf(
                "TASK_ID" to task.id,
                "TASK_DURATION" to task.duration,
                "TASK_NAME" to task.name
            ))
            .build()

        WorkManager.getInstance(TaskCronometerApplication.getAppContext()).enqueue(workRequest)
    }

    private fun updateTimer(task: Task) {

    }


    /**
     * Deletes the item from the [TasksRepository]'s data source.
     */
    suspend fun deleteTask(task: Task) {
        tasksRepository.deleteTask(task)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(
    val taskList: List<Task> = listOf()
)

