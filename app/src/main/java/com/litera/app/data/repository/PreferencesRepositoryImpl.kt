package com.litera.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.litera.app.domain.model.UserPreferences
import com.litera.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private object PreferenceKeys {
    val SELECTED_CATEGORIES = stringPreferencesKey("selected_categories")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val QUIZ_COMPLETED = booleanPreferencesKey("quiz_completed")
}

private const val CATEGORY_DELIMITER = "||"

class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    override fun observePreferences(): Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            selectedCategories = prefs[PreferenceKeys.SELECTED_CATEGORIES]
                ?.split(CATEGORY_DELIMITER)
                ?.filter { it.isNotBlank() }
                ?: emptyList(),
            onboardingCompleted = prefs[PreferenceKeys.ONBOARDING_COMPLETED] ?: false,
            quizCompleted = prefs[PreferenceKeys.QUIZ_COMPLETED] ?: false
        )
    }

    override suspend fun saveSelectedCategories(categories: List<String>) {
        dataStore.edit { prefs ->
            prefs[PreferenceKeys.SELECTED_CATEGORIES] = categories.joinToString(CATEGORY_DELIMITER)
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.ONBOARDING_COMPLETED] = completed }
    }

    override suspend fun setQuizCompleted(completed: Boolean) {
        dataStore.edit { prefs -> prefs[PreferenceKeys.QUIZ_COMPLETED] = completed }
    }
}
