package com.litera.app.domain.repository

import com.litera.app.domain.model.FocusSettings
import kotlinx.coroutines.flow.Flow

interface FocusPreferencesRepository {
    fun observeSettings(): Flow<FocusSettings>
    suspend fun setSelectedDuration(seconds: Int)
    suspend fun setNotificationPrefs(morning: Boolean, afternoon: Boolean, night: Boolean)

    /** Last computed reading pace, in pages/hour, from the "Ritmo de leitura" flow, if any. */
    fun observeLastPagesPerHour(): Flow<Int?>
    suspend fun saveLastPagesPerHour(pagesPerHour: Int)
}
