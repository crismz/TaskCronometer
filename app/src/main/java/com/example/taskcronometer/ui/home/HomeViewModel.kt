package com.example.taskcronometer.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.data.TasksRepository
import com.example.taskcronometer.data.TimeValuesRepository
import com.example.taskcronometer.data.UserPreferencesRepository
import com.example.taskcronometer.utilities.NotificationHelper
import com.example.taskcronometer.utilities.TaskAlarmHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

// TODO("See if there is another way less consuming of resources to do the timers. Check OnSTOP on Activity")

/**
 * ViewModel to manage tasks created.
 */
class HomeViewModel(
    private val tasksRepository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val timeValuesRepository: TimeValuesRepository,
    private val notificationHelper: NotificationHelper,
    private val taskAlarmHelper: TaskAlarmHelper
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

    init {
        notificationHelper.setUpNotificationChannels()
        updateTimersAfterAppOpened()
    }

    /**
     *  Start Timer of task, update the database with paused = false, post Notification
     *  and call updateTimer
     */
    fun startTimerTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.updateTask(
                Task(
                    id = task.id,
                    name = task.name,
                    duration = task.duration,
                    remainingTime = task.remainingTime,
                    paused = false
                )
            )

            // Remaining time minus one second to avoid showing on the notification negative numbers
            taskAlarmHelper.setTaskAlarm(
                task.id,
                task.name,
                (task.remainingTime - 1) * 1000L
            )

            delay(100)

            notificationHelper.postNotification(
                taskId = task.id,
                taskName = task.name,
                remainingTime = task.remainingTime
            )

            updateTimer(task)
        }
    }

    /**
     *  Pause Timer of task, update the database with paused = true and delete Notification
     */
    fun pauseTimerTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.updateTask(
                Task(
                    id = task.id,
                    name = task.name,
                    duration = task.duration,
                    remainingTime = task.remainingTime,
                    paused = true
                )
            )

            taskAlarmHelper.cancelTaskAlarm(task.id)

            notificationHelper.deleteNotification(task.id)
        }
    }

    /**
     * Deletes the item from the [TasksRepository]'s data source.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            tasksRepository.deleteTask(task)
            taskAlarmHelper.cancelTaskAlarm(task.id)
            notificationHelper.deleteNotification(task.id)
        }
    }

    private fun updateTimer(task: Task) {
        var remainingTime = task.remainingTime
        val id = task.id
        val name = task.name
        val duration = task.duration
        // TODO("Chequear cuanto sobra de milisegundos y esperar esa cantidad de
        //  tiempo antes de entrar al while")
        viewModelScope.launch {
            while(homeUiState.value.taskList.find { it.id == id }?.paused == false &&
            remainingTime > 0) {
                remainingTime -= 1000
                val updateTask = Task(id, name, duration, remainingTime, false)
                viewModelScope.launch {
                    tasksRepository.updateTask(updateTask)
                    timeValuesRepository.saveTimeAppClosed(Date().time)
                }
                delay(1000)

            }
        }
    }

    // TODO("Corregir error de tiempo cuando se reabre la app.
    //  Ver de utilizar una variable que guarde el tiempo que fue despausada la tarea")
    private fun updateTimersAfterAppOpened() {
        viewModelScope.launch {
            homeUiState.map { it.taskList }.first { it.isNotEmpty() }
            for (task in homeUiState.value.taskList) {
                if (!task.paused && task.remainingTime > 0) {
                    val timePass: Long = (Date().time - timeValuesRepository.timeAppClosed.first())
                    // Add a second to achieve real time
                    val remainingTime: Long = (task.remainingTime - timePass) + 500
                    val updateTask = Task(
                        task.id,
                        task.name,
                        task.duration,
                        if (remainingTime > 0) remainingTime else 0,
                        false
                    )
                    tasksRepository.updateTask(updateTask)
                    updateTimer(updateTask)
                }
            }
        }
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

