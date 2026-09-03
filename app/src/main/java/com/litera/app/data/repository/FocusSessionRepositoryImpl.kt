package com.litera.app.data.repository

import com.litera.app.data.local.dao.FocusSessionDao
import com.litera.app.data.local.entity.FocusSessionEntity
import com.litera.app.domain.model.FocusSession
import com.litera.app.domain.model.FocusStats
import com.litera.app.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val XP_PER_SESSION = 10

class FocusSessionRepositoryImpl @Inject constructor(
    private val dao: FocusSessionDao
) : FocusSessionRepository {

    override fun observeSessions(): Flow<List<FocusSession>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeStats(): Flow<FocusStats> =
        dao.observeAll().map { list ->
            FocusStats(
                totalFocusSeconds = list.sumOf { it.durationSeconds.toLong() },
                sessionsCompleted = list.size,
                xp = list.size * XP_PER_SESSION
            )
        }

    override suspend fun recordSession(durationSeconds: Int) {
        dao.insert(FocusSessionEntity(durationSeconds = durationSeconds, completedAt = System.currentTimeMillis()))
    }
}

private fun FocusSessionEntity.toDomain() = FocusSession(
    id = id,
    durationSeconds = durationSeconds,
    completedAt = completedAt
)
