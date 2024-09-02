package com.example.taskcronometer.ui.task

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.data.TasksRepository


/**
 * ViewModel to validate and insert tasks in the Room database
 */
class TaskEntryViewModel(private val tasksRepository: TasksRepository): ViewModel() {

    /**
     *  Holds currently task ui state
     */
    var taskUiState by mutableStateOf(TaskUiState())
        private  set

    /**
     * Updates the [taskUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(taskDetails: TaskDetails) {
        taskUiState =
            TaskUiState(taskDetails = taskDetails, isEntryValid = validateInput(taskDetails))
    }

    /**
     * Inserts an [Task] in the Room database
     */
    suspend fun saveTask() {
        if (validateInput()) {
            tasksRepository.insertTask(taskUiState.taskDetails.toTask())
        }
    }

    private fun validateInput(uiState: TaskDetails = taskUiState.taskDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && duration >= 60 && duration <= 86400
        }
    }
}

/**
 * Ui State for TaskEntry.
 */
data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails (
    val id: Int = 0,
    val name: String = "",
    val duration: Int = 0,
)


/**
 * Extension function to convert [TaskUiState] to [Task].
 */
fun TaskDetails.toTask(): Task = Task(
    id = id,
    name = name,
    duration = duration
)

/**
 * Extension function to convert [Task] to [TaskUiState]
 */
fun Task.toTaskUiState(isEntryValid: Boolean = false): TaskUiState = TaskUiState(
    taskDetails = this.toTaskDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Task] to [TaskDetails]
 */
fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    name = name,
    duration = duration
)
