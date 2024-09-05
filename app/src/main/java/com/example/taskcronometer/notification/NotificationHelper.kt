package com.example.taskcronometer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.taskcronometer.R
import com.example.taskcronometer.data.ParseTimeLeft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(private val context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        /**
         * The notification channel for messages.
         */
        private const val CHANNEL_TASKS_RUNNING = "tasks_running"
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun setUpNotificationChannels() {
        if (notificationManager.getNotificationChannel(CHANNEL_TASKS_RUNNING) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_TASKS_RUNNING,
                    context.getString(R.string.tasks_running_channel),
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = context.getString(R.string.tasks_running_channel_description)
                }
            )
        }
    }


    override suspend fun doWork(): Result {
        val taskId: Int = inputData.getInt("TASK_ID", 0)
        val taskDuration: Int = inputData.getInt("TASK_DURATION", 0)
        val taskName: String = inputData.getString("TASK_NAME") ?: ""
        setForegroundAsync(createForegroundInfo(taskId, taskName, taskDuration))
        countDown(taskId, taskName, taskDuration)
        return Result.success()
    }

    private suspend fun countDown(
        taskId: Int,
        taskName: String,
        remainingTime: Int
    ) {
        withContext(Dispatchers.IO) {
            for (i in remainingTime downTo 0) {
                Thread.sleep(1000) // Delay for 1 second

                // Update notification text without showing a progress bar
                setForegroundAsync(createForegroundInfo(taskId, taskName, i))
            }
        }
    }

    private fun createForegroundInfo(
        taskId: Int,
        taskName: String,
        remainingTime: Int
    ): ForegroundInfo {
        val icon = R.drawable.ic_launcher_foreground

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_TASKS_RUNNING)
            .setSmallIcon(icon)
            .setContentTitle(taskName)
            .setContentText("Time remaining: ${ParseTimeLeft.toHHMMSS(remainingTime)} seconds")
            .setOngoing(true)  // Makes notification persistent until manually stopped
            .setCategory(Notification.CATEGORY_PROGRESS)

        val notification = notificationBuilder.build()

        return ForegroundInfo(taskId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)

        /*
        notificationManager.notify(task.id, notificationBuilder.build())


        // Simulate countdown by updating notification every second
        Thread {
            for (i in remainingTime downTo 0) {
                Thread.sleep(1000)  // Delay for 1 second

                // Update notification text without showing a progress bar
                notificationBuilder
                    .setContentText("Time remaining: ${ParseTimeLeft.toHHMMSS(i)} seconds")
                notificationManager.notify(task.id, notificationBuilder.build())
            }

            // Once the countdown is done, update the notification
            notificationBuilder.setContentText("Timer finished")
                .setOngoing(false) // Make notification dismissible
            notificationManager.notify(task.id, notificationBuilder.build())
        }.start()
         */
    }

}

