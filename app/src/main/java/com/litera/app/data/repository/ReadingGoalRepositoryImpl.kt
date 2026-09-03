package com.litera.app.data.repository

import com.litera.app.data.local.dao.ReadingGoalDao
import com.litera.app.data.local.entity.ReadingGoalEntity
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ReadingGoalType
import com.litera.app.domain.repository.ReadingGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReadingGoalRepositoryImpl @Inject constructor(
    private val dao: ReadingGoalDao
) : ReadingGoalRepository {

    override fun observeGoals(): Flow<List<ReadingGoal>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getGoal(id: Long): ReadingGoal? = dao.getById(id)?.toDomain()

    override suspend fun saveGoal(goal: ReadingGoal): Long = dao.upsert(goal.toEntity())

    override suspend fun deleteGoal(id: Long) {
        val existing = dao.getById(id) ?: return
        dao.delete(existing)
    }
}

private fun ReadingGoalEntity.toDomain() = ReadingGoal(
    id = id,
    label = label,
    type = runCatching { ReadingGoalType.valueOf(type) }.getOrDefault(ReadingGoalType.PAGES_PER_WEEK),
    targetValue = targetValue,
    progressValue = progressValue,
    createdAt = createdAt
)

private fun ReadingGoal.toEntity() = ReadingGoalEntity(
    id = id,
    label = label,
    type = type.name,
    targetValue = targetValue,
    progressValue = progressValue,
    createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis()
)
