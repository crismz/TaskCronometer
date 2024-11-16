package com.example.taskcronometer.ui.task

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskcronometer.R
import com.example.taskcronometer.TaskCronometerTopAppBar
import com.example.taskcronometer.ui.TaskCronometerAppViewModelProvider
import com.example.taskcronometer.ui.navigation.NavigationDestination
import com.example.taskcronometer.ui.theme.TaskCronometerTheme
import com.example.taskcronometer.utilities.TimeHelper

object TaskEntryDestination : NavigationDestination {
    override val route = "task_entry"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: TaskEntryViewModel = viewModel(factory = TaskCronometerAppViewModelProvider.Factory)
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TaskCronometerTopAppBar(
                title = stringResource(R.string.new_task_title),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        TaskEntryBody(
            taskUiState = viewModel.taskUiState,
            onDetailsValueChange = viewModel::updateUiStateTaskDetails,
            onSelDurationValueChange = viewModel::updateUiStateSelectDuration,
            onSaveClick = {
                viewModel.saveTask()
                navigateBack()
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            keyboardController?.hide()
                        }
                    )
                }
        )
    }
}

@Composable
private fun TaskEntryBody(
    taskUiState: TaskUiState,
    onDetailsValueChange: (TaskDetails) -> Unit,
    onSelDurationValueChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TaskInputForm(
            taskDetails = taskUiState.taskDetails,
            selectDuration = taskUiState.selectDuration,
            onDetailsValueChange = onDetailsValueChange,
            onSelDurationValueChange = onSelDurationValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = taskUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskInputForm(
    taskDetails: TaskDetails,
    selectDuration: Boolean,
    modifier: Modifier = Modifier,
    onDetailsValueChange: (TaskDetails) -> Unit = {},
    onSelDurationValueChange: (Boolean) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_entryfields))
    ) {
        InputNameRow(
            taskDetails = taskDetails,
            onDetailsValueChange = onDetailsValueChange,
            onSelDurationValueChange = onSelDurationValueChange,
            enabled = enabled
        )
        InputDurationRow(
            dataRequired = R.string.task_duration_req,
            onTaskSelectDurationValueChange = onSelDurationValueChange,
            taskDetails = taskDetails
        )
    }

    if (selectDuration) {
        val durationPickerState = rememberTimePickerState(
            initialHour = TimeHelper.obtainHours(taskDetails.duration),
            initialMinute = TimeHelper.obtainMinutes(taskDetails.duration),
            is24Hour = true
        )

        DurationPickerDialog(
            onDismiss = { onSelDurationValueChange(false) },
            onConfirm = {
                onDetailsValueChange(taskDetails.copy(
                    duration = TimeHelper.toMilliSeconds(
                        durationPickerState.hour,
                        durationPickerState.minute
                    )
                ) )
                onSelDurationValueChange(false)
            }
        ) {
            TimeInput(state = durationPickerState)
        }
    }
}

@Composable
private fun InputNameRow(
    taskDetails: TaskDetails,
    onDetailsValueChange: (TaskDetails) -> Unit,
    enabled: Boolean,
    onSelDurationValueChange: (Boolean) -> Unit
) {
    val maxLength = 50
    var showMessage by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = taskDetails.name,
        onValueChange = { newName ->
            if (newName.length <= maxLength) {
                onDetailsValueChange(taskDetails.copy(name = newName))
            } else {
                showMessage = true
            }
        },
        label = {
            Text(
                text = stringResource(R.string.task_name_req),
                style = MaterialTheme.typography.labelSmall
            )
        },
        supportingText = {
            if (showMessage) {
                Text(
                    text = "Task name cannot exceed $maxLength characters",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.primary
        ),
        textStyle = MaterialTheme.typography.bodyLarge,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onSelDurationValueChange(true)
            }
        )
    )
}


@Composable
private fun InputDurationRow(
    dataRequired: Int,
    onTaskSelectDurationValueChange: (Boolean) -> Unit,
    taskDetails: TaskDetails
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(dataRequired),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        TextButton(
            onClick = { onTaskSelectDurationValueChange(true) },
            contentPadding = PaddingValues(
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.small
        ) {
            DataDuration(taskDetails)
        }
    }
}

@Composable
private fun DataDuration(taskDetails: TaskDetails) {
    if (taskDetails.duration == 0.toLong()) {
        Text(
            text = "00:00",
            style = MaterialTheme.typography.labelMedium
        )
    } else {
        Text(
            text = TimeHelper.toHHMM(taskDetails.duration),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun DurationPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.confirm))
            }
        },
        title = { Text("Select Duration") },
        text = { content() }
    )
}

@Preview
@Composable
fun TaskEntryScreenPreview() {
    TaskCronometerTheme {
        Scaffold { innerPadding ->
            TaskEntryBody(
                taskUiState = TaskUiState(TaskDetails(name = "New Task", duration = 1)),
                onDetailsValueChange = {},
                onSelDurationValueChange = {},
                onSaveClick = {},
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    )
                    .fillMaxSize()
            )
        }
    }
}

