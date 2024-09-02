package com.example.taskcronometer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val duration: Int,
    val paused: Boolean = true
)
