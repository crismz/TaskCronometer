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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.taskcronometer.ui.task.TaskItem
import com.example.taskcronometer.ui.navigation.NavigationDestination
import com.example.taskcronometer.ui.theme.TaskCronometerTheme
import kotlinx.coroutines.launch


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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

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
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.deleteTask(it)
                }
            },
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun HomeBody(
    tasks: List<Task>,
    onDelete: (Task) -> Unit,
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
                onDelete = onDelete,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items = tasks, key = { it.id }) {task ->
            TaskItem(
                task = task,
                { onDelete(task) }
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
    val task = Task(1, "Trabajar en el projecto TaskCronometer", 63000, true)
    val task50 = Task(2, "50 caracteres aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        63000,
        true)
    val task100 = Task(3,"100 caracteres aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbfdlkgjdfkljgkfdjkljwklwalkjzjlwkljkdlsas",
        63000,
        true)
    TaskCronometerTheme {
        Surface (color = MaterialTheme.colorScheme.background) {
            TaskList(
                listOf(task,task50, task100),
                onDelete = {}
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
                onDelete = { },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}