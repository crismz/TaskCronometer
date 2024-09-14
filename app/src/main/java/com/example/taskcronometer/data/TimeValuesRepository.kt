package com.example.taskcronometer.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TimeValuesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val TIME_APP_CLOSED = longPreferencesKey("time_app_closed")
        const val TAG = "TimeValuesRepo"

    }

    val timeAppClosed: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[TIME_APP_CLOSED] ?: 0
        }

    suspend fun saveTimeAppClosed(timeAppClosed: Long) {
        dataStore.edit { preferences ->
            preferences[TIME_APP_CLOSED] = timeAppClosed
        }
    }

}