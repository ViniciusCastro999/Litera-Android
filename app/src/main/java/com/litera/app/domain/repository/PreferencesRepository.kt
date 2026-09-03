package com.litera.app.domain.repository

import com.litera.app.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun saveSelectedCategories(categories: List<String>)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setQuizCompleted(completed: Boolean)
}
