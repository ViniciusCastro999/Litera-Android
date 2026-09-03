package com.litera.app.domain.repository

import com.litera.app.domain.model.ReadingGoal
import kotlinx.coroutines.flow.Flow

interface ReadingGoalRepository {
    fun observeGoals(): Flow<List<ReadingGoal>>
    suspend fun getGoal(id: Long): ReadingGoal?
    suspend fun saveGoal(goal: ReadingGoal): Long
    suspend fun deleteGoal(id: Long)
}
