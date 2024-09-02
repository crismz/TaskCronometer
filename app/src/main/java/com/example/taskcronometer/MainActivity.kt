package com.example.taskcronometer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.taskcronometer.ui.theme.TaskCronometerTheme

class MainActivity : ComponentActivity() {
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
    }
}
