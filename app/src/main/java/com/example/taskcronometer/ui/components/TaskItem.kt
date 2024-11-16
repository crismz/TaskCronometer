package com.example.taskcronometer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskcronometer.R
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.ui.theme.Shapes
import com.example.taskcronometer.ui.theme.TaskCronometerTheme
import com.example.taskcronometer.utilities.TimeHelper

/**
 * TaskItem shows a card with the name and time of the task.
 */
@Composable
fun TaskItem (
    task: Task,
    timer: Long?,
    onDelete: () -> Unit,
    onStartTaskTimer: () -> Unit,
    onPauseTaskTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        onClick = if (task.paused) onStartTaskTimer else onPauseTaskTimer,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        shape = Shapes.small,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(6f)
                    .padding(end = dimensionResource(R.dimen.padding_medium))
            )
            TimerDisplay(
                timer = timer,
                timeRunning = task.timeRunning,
                duration = task.duration,
                modifier = Modifier.weight(2f)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Button to delete task"
                )
            }
        }
    }
}

/**
 * Composable to display the timer in an card.
 */
@Composable
fun TimerDisplay(
    timer: Long?,
    timeRunning: Long,
    duration: Long,
    modifier: Modifier = Modifier
) {
    val formattedTimeRunning = TimeHelper.toHHMMSS(timer ?: timeRunning)
    val formattedDuration = TimeHelper.toHHMMSS(duration)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        // Display current time
        Text(
            text = formattedTimeRunning,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
            )

        // Custom diagonal divider
        DiagonalDivider()

        // Display total duration
        Text(
            text = formattedDuration,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Composable to divide the running time of the total duration of a task.
 */
@Composable
fun DiagonalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.height_diagonal_divider))
    ) {
        val lineColor: Color = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = 0f
            val startY = size.height
            val endX = size.width
            val endY = size.height - (size.width * 0.13163f)


            drawLine(
                color = lineColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview
@Composable
fun TaskItemPreview() {
    val task = Task(
        1,
        "Work in project TaskCronometer. Text to make the name larger.",
        7200000,
        timeRunning = 7200000 - 6300000,
        true,
        0,
        0)
    TaskCronometerTheme {
        TaskItem(
            task,
            6300000,
            onDelete = {},
            onStartTaskTimer = {},
            onPauseTaskTimer = {})
    }
}