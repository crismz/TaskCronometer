package com.example.taskcronometer

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.taskcronometer.data.AppContainer
import com.example.taskcronometer.data.AppDataContainer
import com.example.taskcronometer.data.TimeValuesRepository
import com.example.taskcronometer.data.UserPreferencesRepository

private const val SETTINGS_PREFERENCES_NAMES = "settings_preferences"
private const val TIMES_VALUES = "times_values"

private val Context.dataStoreSettings: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_PREFERENCES_NAMES
)
private val Context.dataStoreTimes: DataStore<Preferences> by preferencesDataStore(
    name = TIMES_VALUES
)

class TaskCronometerApplication: Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies.
     * DataStores to save and obtain configurations and utility data.
     */
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository
    lateinit var timeValuesRepository: TimeValuesRepository

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStoreSettings)
        timeValuesRepository = TimeValuesRepository(dataStoreTimes)
    }
}