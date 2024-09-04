package com.example.taskcronometer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskcronometer.R
import com.example.taskcronometer.data.ParseTimeLeft
import com.example.taskcronometer.data.Task
import com.example.taskcronometer.ui.theme.Shapes
import com.example.taskcronometer.ui.theme.TaskCronometerTheme

//TODO(Second: Update UI with duration left overtime)
//TODO(Third: Show when timer is on)
//TODO(Show notification of timer)

@Composable
fun TaskItem (
    task: Task,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card (
        onClick = onClick,
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
            Text(
                text = ParseTimeLeft.toHHMMSS(task.duration),
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier
                    .weight(2f)
                    .wrapContentWidth()
            )
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

@Preview
@Composable
fun TaskItemPreview() {
    val task = Task(1,"Trabajar en el projecto TaskCronometer adasdasdassadasas", 63000, true)
    TaskCronometerTheme {
        TaskItem(task, onDelete = {}, onClick = {})
    }
}