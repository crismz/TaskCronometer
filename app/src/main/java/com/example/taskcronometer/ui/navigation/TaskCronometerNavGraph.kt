package com.example.taskcronometer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.taskcronometer.ui.home.HomeDestination
import com.example.taskcronometer.ui.home.HomeScreen
import com.example.taskcronometer.ui.task.TaskEntryDestination
import com.example.taskcronometer.ui.task.TaskEntryScreen

/**
 * Provides Navigation graph for the application
 */
@Composable
fun TaskCronometerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToTaskEntry = { navController.navigate(TaskEntryDestination.route)}
            )
        }
        composable(route = TaskEntryDestination.route) {
            TaskEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}