package com.example.taskcronometer.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TaskAlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("TASK_ID", -1)
        val taskName = intent.getStringExtra("TASK_NAME")

        if (taskId != -1) {
            showNotification(context, taskId, taskName ?: "")
        }
    }

    private fun showNotification(context: Context, taskId: Int, taskName: String) {
        val notificationHelper = NotificationHelper(context)

        notificationHelper.setUpNotificationChannels()

        notificationHelper.postNotification(
            taskId = taskId,
            taskName = taskName,
            isFinish = true,
        )
    }
}