package com.example.taskcronometer.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskcronometer.R
import com.example.taskcronometer.TaskCronometerTopAppBar
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.ui.TaskCronometerAppViewModelProvider
import com.example.taskcronometer.ui.components.TaskItem
import com.example.taskcronometer.ui.navigation.NavigationDestination
import com.example.taskcronometer.ui.theme.TaskCronometerTheme


object HomeDestination: NavigationDestination {
    override val route = "home"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToTaskEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = TaskCronometerAppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val timersUiState by viewModel.timers.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TaskCronometerTopAppBar(
                title = stringResource(R.string.app_name),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButtonHomeScreen(onClickButton = navigateToTaskEntry)
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) { innerPadding ->
        HomeBody(
            tasks = homeUiState.taskList,
            timers = timersUiState,
            onDelete = {
                viewModel.deleteTask(it)
            },
            onStartTaskTimer = {
                viewModel.startTimerTask(it)
            },
            onPauseTaskTimer = {
                viewModel.pauseTimerTask(it)
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun HomeBody(
    tasks: List<Task>,
    timers: Map<Int, Long>,
    onDelete: (Task) -> Unit,
    onStartTaskTimer: (Task) -> Unit,
    onPauseTaskTimer: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.empty_home),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
        } else {
            TaskList(
                tasks = tasks,
                timers,
                onDelete = onDelete,
                onStartTaskTimer = onStartTaskTimer,
                onPauseTaskTimer = onPauseTaskTimer,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    timers: Map<Int, Long>,
    onDelete: (Task) -> Unit,
    onStartTaskTimer: (Task) -> Unit,
    onPauseTaskTimer: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = tasks, key = { it.id }) {task ->
            TaskItem(
                task = task,
                timer = timers[task.id],
                onDelete = { onDelete(task) },
                onStartTaskTimer = { onStartTaskTimer(task) },
                onPauseTaskTimer = { onPauseTaskTimer(task) }
            )
        }
    }
}

@Composable
private fun FloatingActionButtonHomeScreen(
    onClickButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClickButton,
        modifier = modifier
    ) {
        Icon(Icons.Filled.Add, stringResource(R.string.button_to_add_task) )
    }
}


@Preview
@Composable
fun TaskListPreview() {
    val task = Task(1, "Work on the project TaskCronometer",
        63000, 0,true, 0, 0)
    val task50 = Task(2, "50 characters aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        63000, 0,true, 0, 0)
    val task100 = Task(3,"100 characters aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbfdlkgjdfkljgkfdjkljwklwalkjzjlwkljkdlsas",
        63000, 0,true, 0, 0)
    TaskCronometerTheme {
        Surface (color = MaterialTheme.colorScheme.background) {
            TaskList(
                listOf(task,task50, task100),
                timers = mapOf(1 to 63000, 2 to 63000, 3 to 63000),
                onDelete = {},
                onStartTaskTimer = {},
                onPauseTaskTimer = {}
            )
        }
    }
}

@Preview
@Composable
fun HomeBodyPreview() {
    TaskCronometerTheme {
        Scaffold { innerPadding ->
            HomeBody(
                tasks = listOf(),
                timers = mapOf(),
                onDelete = { },
                onStartTaskTimer = {},
                onPauseTaskTimer = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}