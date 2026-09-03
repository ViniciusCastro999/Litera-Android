package com.litera.app.domain.usecase

import com.litera.app.domain.repository.FocusPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLastPagesPerHourUseCase @Inject constructor(
    private val repository: FocusPreferencesRepository
) {
    operator fun invoke(): Flow<Int?> = repository.observeLastPagesPerHour()
}

class SaveLastPagesPerHourUseCase @Inject constructor(
    private val repository: FocusPreferencesRepository
) {
    suspend operator fun invoke(pagesPerHour: Int) = repository.saveLastPagesPerHour(pagesPerHour)
}
