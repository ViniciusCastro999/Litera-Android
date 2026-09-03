package com.litera.app.domain.repository

import com.litera.app.domain.model.FocusSession
import com.litera.app.domain.model.FocusStats
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    fun observeSessions(): Flow<List<FocusSession>>
    fun observeStats(): Flow<FocusStats>
    suspend fun recordSession(durationSeconds: Int)
}
