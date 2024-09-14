package com.example.taskcronometer.utilities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat.startActivity
import java.util.Date

class TaskAlarmHelper(private val context: Context) {
    val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Intent to trigger when the alarm fires
    fun setTaskAlarm(taskId:Int, taskName:String, remainingTimeMillis: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            // TODO("See if ask again for permission to set alarms")
        } else {

            val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
                putExtra("TASK_ID", taskId)
                putExtra("TASK_NAME",taskName)
            }

            // PendingIntent that will be triggered when the alarm goes off
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Schedule the alarm to go off when the timer finishes
            val triggerTime = Date().time + remainingTimeMillis
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelTaskAlarm(taskId: Int) {
        val intent = Intent(context, TaskAlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}