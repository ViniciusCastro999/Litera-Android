package com.litera.app.domain.usecase

import com.litera.app.core.common.Constants
import com.litera.app.core.common.Resource
import com.litera.app.domain.model.UserPreferences
import com.litera.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePreferencesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    operator fun invoke(): Flow<UserPreferences> = repository.observePreferences()
}

class SaveSelectedCategoriesUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(categories: List<String>): Resource<Unit> {
        if (categories.size < Constants.MIN_ONBOARDING_CATEGORIES) {
            return Resource.Error("Selecione pelo menos ${Constants.MIN_ONBOARDING_CATEGORIES} categorias.")
        }
        repository.saveSelectedCategories(categories)
        repository.setQuizCompleted(true)
        return Resource.Success(Unit)
    }
}

class SetOnboardingCompletedUseCase @Inject constructor(
    private val repository: PreferencesRepository
) {
    suspend operator fun invoke(completed: Boolean = true) = repository.setOnboardingCompleted(completed)
}
