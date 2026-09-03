package com.litera.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.litera.app.domain.model.FocusSettings
import com.litera.app.domain.repository.FocusPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private object FocusPreferenceKeys {
    val SELECTED_DURATION_SECONDS = intPreferencesKey("focus_selected_duration_seconds")
    val NOTIFY_MORNING = booleanPreferencesKey("focus_notify_morning")
    val NOTIFY_AFTERNOON = booleanPreferencesKey("focus_notify_afternoon")
    val NOTIFY_NIGHT = booleanPreferencesKey("focus_notify_night")
    val LAST_PAGES_PER_HOUR = intPreferencesKey("reading_pace_last_pages_per_hour")
}

class FocusPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : FocusPreferencesRepository {

    override fun observeSettings(): Flow<FocusSettings> = dataStore.data.map { prefs ->
        val defaults = FocusSettings()
        FocusSettings(
            selectedDurationSeconds = prefs[FocusPreferenceKeys.SELECTED_DURATION_SECONDS]
                ?: defaults.selectedDurationSeconds,
            notifyMorning = prefs[FocusPreferenceKeys.NOTIFY_MORNING] ?: false,
            notifyAfternoon = prefs[FocusPreferenceKeys.NOTIFY_AFTERNOON] ?: false,
            notifyNight = prefs[FocusPreferenceKeys.NOTIFY_NIGHT] ?: false
        )
    }

    override suspend fun setSelectedDuration(seconds: Int) {
        dataStore.edit { prefs -> prefs[FocusPreferenceKeys.SELECTED_DURATION_SECONDS] = seconds }
    }

    override suspend fun setNotificationPrefs(morning: Boolean, afternoon: Boolean, night: Boolean) {
        dataStore.edit { prefs ->
            prefs[FocusPreferenceKeys.NOTIFY_MORNING] = morning
            prefs[FocusPreferenceKeys.NOTIFY_AFTERNOON] = afternoon
            prefs[FocusPreferenceKeys.NOTIFY_NIGHT] = night
        }
    }

    override fun observeLastPagesPerHour(): Flow<Int?> =
        dataStore.data.map { prefs -> prefs[FocusPreferenceKeys.LAST_PAGES_PER_HOUR] }

    override suspend fun saveLastPagesPerHour(pagesPerHour: Int) {
        dataStore.edit { prefs -> prefs[FocusPreferenceKeys.LAST_PAGES_PER_HOUR] = pagesPerHour }
    }
}
