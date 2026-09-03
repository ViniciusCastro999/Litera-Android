package com.litera.app.domain.usecase

import com.litera.app.domain.model.FocusSession
import com.litera.app.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFocusSessionsUseCase @Inject constructor(
    private val repository: FocusSessionRepository
) {
    operator fun invoke(): Flow<List<FocusSession>> = repository.observeSessions()
}
