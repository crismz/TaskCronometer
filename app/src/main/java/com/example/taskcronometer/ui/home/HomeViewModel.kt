package com.example.taskcronometer.ui.home


import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.data.TasksRepository
import com.example.taskcronometer.data.UserPreferencesRepository
import com.example.taskcronometer.utilities.NotificationHelper
import com.example.taskcronometer.utilities.TaskAlarmHelper
import com.example.taskcronometer.utilities.TimeHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel to manage tasks created.
 */
class HomeViewModel(
    private val tasksRepository: TasksRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
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

    /**
     * MutableStateFlow to hold timers (task ID to running time).
     * Used for showing the time in the UI.
     */
    private val _timers = MutableStateFlow<Map<Int, Long>>(emptyMap())
    val timers: StateFlow<Map<Int, Long>> = _timers.asStateFlow()

    /**
     * MutableMap to hold the CountDownTimers (task ID to CountDownTimer)
     * Used for calculate the timer of each task.
     */
    private val countDownTimers: MutableMap<Int, CountDownTimer> = mutableMapOf()

    init {
        viewModelScope.launch {
            notificationHelper.setUpNotificationChannels()

            // Update Timers on database
            updateTimersOnDatabase()
        }
    }

    /**
     *  Start Timer of task, update the database with paused = false, post Notification,
     *  set Alarm and call updateTimer
     */
    fun startTimerTask(task: Task) {
        viewModelScope.launch {
            updateTimer(task)

            tasksRepository.updateTask(
                Task(
                    id = task.id,
                    name = task.name,
                    duration = task.duration,
                    timeRunning = task.timeRunning,
                    paused = false,
                    lastTimeResumed = Date().time,
                    lastTimePaused = task.lastTimePaused
                )
            )

            if (task.timeRunning < task.duration) {
                // Remaining time minus one second to avoid showing on the notification negative numbers
                taskAlarmHelper.setTaskAlarm(
                    task.id,
                    task.name,
                    ((task.duration - task.timeRunning) - 1)
                )
            }

            notificationHelper.postNotification(
                taskId = task.id,
                taskName = task.name,
                timeRunning = task.timeRunning
            )
        }
    }

    /**
     *  Pause Timer of task, update the database with paused = true and delete Notification
     */
    fun pauseTimerTask(task: Task) {
        viewModelScope.launch {
            countDownTimers[task.id]?.cancel()

            val now = Date().time

            val actualTimeRunning = TimeHelper.obtainActualTimeRunning(
                now,
                task.timeRunning,
                task.lastTimeResumed
            )

            tasksRepository.updateTask(
                Task(
                    id = task.id,
                    name = task.name,
                    duration = task.duration,
                    timeRunning = actualTimeRunning,
                    paused = true,
                    lastTimeResumed = task.lastTimeResumed,
                    lastTimePaused = now
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
        }
        taskAlarmHelper.cancelTaskAlarm(task.id)
        notificationHelper.deleteNotification(task.id)
    }

    private fun updateTimer(task: Task) {
        var time = task.timeRunning
        var remainingTime = Long.MAX_VALUE
        val id = task.id

        // TODO (Add an option to decide if the timer is paused when it completes)

        viewModelScope.launch {
            countDownTimers[id] = object :CountDownTimer(remainingTime, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    time += (remainingTime - millisUntilFinished)
                    _timers.value = _timers.value.toMutableMap().apply {
                        this[id] = time
                    }
                    remainingTime = millisUntilFinished
                }

                override fun onFinish() {
                    pauseTimerTask(task)
                }
            }.start()
        }
    }

    private suspend fun updateTimersOnDatabase() {
        val taskList: List<Task> =  tasksRepository.getAllTasksStream().first()

        for (task in taskList) {
            if (!task.paused) {
                val now = Date().time
                val actualTime = TimeHelper.obtainActualTimeRunning(
                    now,
                    task.timeRunning,
                    task.lastTimeResumed
                )

                val updateTask = Task(
                    id = task.id,
                    name = task.name,
                    duration = task.duration,
                    timeRunning = actualTime,
                    paused = false,
                    lastTimeResumed = now,
                    lastTimePaused = task.lastTimePaused
                )
                tasksRepository.updateTask(updateTask)
                updateTimer(updateTask)
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