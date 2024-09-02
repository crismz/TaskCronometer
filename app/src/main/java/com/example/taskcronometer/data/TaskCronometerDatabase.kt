package com.example.taskcronometer.data


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskCronometerDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: TaskCronometerDatabase? = null

        fun getDatabase(content: Context): TaskCronometerDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(content, TaskCronometerDatabase::class.java, "task_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
