package com.example.taskcronometer.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskcronometer.TaskCronometerApplication
import com.example.taskcronometer.ui.home.HomeViewModel
import com.example.taskcronometer.ui.task.TaskEntryViewModel

/**
 * Provides Factory to create a instance of ViewModel for the entire TaskCronometer app
 */
object TaskCronometerAppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for TaskEntryViewModel
        initializer {
            TaskEntryViewModel(taskCronometerApplication().container.tasksRepository)
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(taskCronometerApplication().container.tasksRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and return an instance of
 * [TaskCronometerApplication]
 */
fun CreationExtras.taskCronometerApplication(): TaskCronometerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TaskCronometerApplication)