package com.litera.app.domain.usecase

import com.litera.app.domain.model.FocusSettings
import com.litera.app.domain.model.FocusStats
import com.litera.app.domain.repository.FocusPreferencesRepository
import com.litera.app.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFocusStatsUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    operator fun invoke(): Flow<FocusStats> = repository.observeStats()
}

class RecordFocusSessionUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    suspend operator fun invoke(durationSeconds: Int) = repository.recordSession(durationSeconds)
}

class ObserveFocusSettingsUseCase @Inject constructor(
    private val repository: FocusPreferencesRepository
) {
    operator fun invoke(): Flow<FocusSettings> = repository.observeSettings()
}

class SetFocusDurationUseCase @Inject constructor(
    private val repository: FocusPreferencesRepository
) {
    suspend operator fun invoke(seconds: Int) = repository.setSelectedDuration(seconds)
}

class SetFocusNotificationPrefsUseCase @Inject constructor(
    private val repository: FocusPreferencesRepository
) {
    suspend operator fun invoke(morning: Boolean, afternoon: Boolean, night: Boolean) =
        repository.setNotificationPrefs(morning, afternoon, night)
}
