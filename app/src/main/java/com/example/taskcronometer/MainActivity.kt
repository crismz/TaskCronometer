package com.example.taskcronometer

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.taskcronometer.ui.theme.TaskCronometerTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {

        }

    /**
     * Check for notification permission before starting the service so that the notification is visible
     */
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    // permission already granted
                }

                else -> {
                    notificationPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    /**
     * Check if the app can schedule exact alarms. If not, request the SCHEDULE_EXACT_ALARM permission
      */
    private fun checkAndRequestAlarmsPermission() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
             !alarmManager.canScheduleExactAlarms()) {

            // Show a dialog explaining why the app needs the permission
            AlertDialog.Builder(this).apply {
                setTitle("Permission Required")
                setMessage(
                    "This app requires the ability to schedule exact alarms for reminders. " +
                    "This ensures accuracy. You can decline this permission, " +
                    "but certain features may not work as intended."
                )
                setPositiveButton("Grant Permission") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    intent.data = Uri.fromParts("package", packageName, null)
                    startActivity(intent)
                }
                setNegativeButton("No, Thanks") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        "Exact alarm scheduling may not work without this permission.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            TaskCronometerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TaskCronometerApp()
                }
            }
        }

        checkAndRequestNotificationPermission()
        checkAndRequestAlarmsPermission()
    }

}
