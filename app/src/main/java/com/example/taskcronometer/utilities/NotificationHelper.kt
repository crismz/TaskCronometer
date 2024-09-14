package com.example.taskcronometer.utilities

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.taskcronometer.R
import java.util.Date

class NotificationHelper(private val context: Context) {

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

    fun postNotification(
        taskId: Int,
        taskName: String,
        remainingTime: Long = 0,
        isFinish: Boolean = false
    ) {
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    when (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    )) {
                        PackageManager.PERMISSION_GRANTED -> {
                            // permission already granted
                        }

                        else -> {
                            ActivityResultContracts.RequestPermission()
                        }
                    }
                }
                return
            }


            val notification: Notification = if (isFinish) {
                createNotificationTaskFinished(taskName)
            } else {
                createNotificationTaskRunning(taskName, remainingTime)
            }

            notify(taskId, notification)
        }
    }

    fun deleteNotification(
        taskId: Int
    ) {
        with(NotificationManagerCompat.from(context)) {
            cancel(taskId)
        }
    }

    private fun createNotificationTaskRunning(
        taskName: String,
        remainingTime: Long
    ): Notification {
        val icon = R.drawable.ic_launcher_foreground

        val expireTimeInMilliSeconds = Date().time + remainingTime

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_TASKS_RUNNING)
            .setSmallIcon(icon)
            .setContentTitle(taskName)
            .setChronometerCountDown(true)
            .setUsesChronometer(true)
            .setWhen(expireTimeInMilliSeconds)
            .setShowWhen(true)
            .setOngoing(true)
            .setGroup(taskName)
            .setCategory(Notification.CATEGORY_PROGRESS)

       return notificationBuilder.build()

    }

    private fun createNotificationTaskFinished(
        taskName: String
    ): Notification {
        val icon = R.drawable.ic_launcher_foreground
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_TASKS_RUNNING)
            .setSmallIcon(icon)
            .setContentTitle(taskName)
            .setContentText("Time for task has already finish")
            .setGroup(taskName)
            .setCategory(Notification.CATEGORY_PROGRESS)
            .setSound(alarmSound)

        return notificationBuilder.build()
    }

}

