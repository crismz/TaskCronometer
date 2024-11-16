package com.example.taskcronometer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val duration: Long,
    val timeRunning: Long = 0,
    val paused: Boolean = true,
    val lastTimePaused: Long,
    val lastTimeResumed: Long
)
