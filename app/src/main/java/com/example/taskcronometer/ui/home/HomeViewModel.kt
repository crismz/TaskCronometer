package com.example.taskcronometer.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.data.TasksRepository
import com.example.taskcronometer.service.TimeServiceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel to ...
 */
class HomeViewModel(private val tasksRepository: TasksRepository): ViewModel() {
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

    /**
     * Starts the timer of a task, call updateTimer which updates the database every second
     * with the remaining time
     */
    private fun startTimer(task: Task) {
        val id: Int = task.id
        val duration: Int = task.duration
        TimeServiceManager.startTimer(id, duration)

        updateTimer(task)
    }

    /**
     * Pauses the timer of a task
     */
    private fun pauseTimer(task: Task) {
        val id: Int = task.id
        TimeServiceManager.pauseTimer(id)
    }

    /**
     * Resumes the timer of a task, call updateTimer which updates the database every second
     * with the remaining time
     */
    private fun resumeTimer(task: Task) {
        val id: Int = task.id
        TimeServiceManager.resumeTimer(id)

        updateTimer(task)
    }

    private fun updateTimer(task: Task) {
        viewModelScope.launch {
            delay(100L)
            while (TimeServiceManager.getIsBound()
                && TimeServiceManager.getTimerPaused(task.id) != true
            ) {
                val remainingTime = TimeServiceManager.getRemainingTime(task.id)
                val updateTask = Task(
                    id = task.id,
                    name = task.name,
                    duration = remainingTime,
                    paused = task.paused
                )
                tasksRepository.updateTask(updateTask)
                delay(1000L) // Update every second
                if (remainingTime <= 0) break
            }
        }
    }

    /**
     * Function to decide which function call
     */
    fun startResumeOrPauseTaskTimer(task: Task) {
        viewModelScope.launch {
            if (TimeServiceManager.getIsBound()) {
                when (TimeServiceManager.getTimerPaused(task.id)) {
                    null -> startTimer(task)
                    false -> pauseTimer(task)
                    true -> resumeTimer(task)
                }
            } else {
                // Handle the case where the service is not bound
                // You might want to show a message or attempt to bind the service again
                // Retry binding or show a message to the user
                // Example: Retry after a delay
                delay(500L)
                startResumeOrPauseTaskTimer(task)
            }
        }
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

