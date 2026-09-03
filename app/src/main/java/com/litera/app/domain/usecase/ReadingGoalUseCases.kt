package com.litera.app.domain.usecase

import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.repository.ReadingGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveReadingGoalsUseCase @Inject constructor(
    private val repository: ReadingGoalRepository
) {
    operator fun invoke(): Flow<List<ReadingGoal>> = repository.observeGoals()
}

class GetReadingGoalUseCase @Inject constructor(
    private val repository: ReadingGoalRepository
) {
    suspend operator fun invoke(id: Long): ReadingGoal? = repository.getGoal(id)
}

class SaveReadingGoalUseCase @Inject constructor(
    private val repository: ReadingGoalRepository
) {
    suspend operator fun invoke(goal: ReadingGoal): Long = repository.saveGoal(goal)
}

class DeleteReadingGoalUseCase @Inject constructor(
    private val repository: ReadingGoalRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteGoal(id)
}
