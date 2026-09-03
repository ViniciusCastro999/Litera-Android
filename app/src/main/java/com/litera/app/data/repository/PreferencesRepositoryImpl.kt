@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.litera.app.data.local.OnboardingPreferences
import com.litera.app.data.remote.currentUserIdFlow
import com.litera.app.data.remote.safeWrite
import com.litera.app.data.remote.snapshotsFlow
import com.litera.app.data.remote.userSettingsDoc
import com.litera.app.domain.model.UserPreferences
import com.litera.app.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val DOC = "preferences"

/**
 * Quiz categories + quizCompleted live in Firestore (per account, follow
 * the user across devices). onboardingCompleted stays local — see
 * [OnboardingPreferences] for why it can't be per-account.
 */
class PreferencesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val onboardingPreferences: OnboardingPreferences
) : PreferencesRepository {

    private fun doc(uid: String) = firestore.userSettingsDoc(uid, DOC)

    private fun observeCloudPreferences(): Flow<UserPreferences> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(UserPreferences())
            } else {
                doc(uid).snapshotsFlow().map { it.toObject(UserPreferencesDto::class.java)?.toDomain() ?: UserPreferences() }
            }
        }

    override fun observePreferences(): Flow<UserPreferences> =
        combine(observeCloudPreferences(), onboardingPreferences.completed) { cloudPrefs, onboardingCompleted ->
            cloudPrefs.copy(onboardingCompleted = onboardingCompleted)
        }

    override suspend fun saveSelectedCategories(categories: List<String>) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        doc(uid).set(mapOf("selectedCategories" to categories), SetOptions.merge()).await()
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingPreferences.setCompleted(completed)
    }

    override suspend fun setQuizCompleted(completed: Boolean) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        doc(uid).set(mapOf("quizCompleted" to completed), SetOptions.merge()).await()
    }
}

data class UserPreferencesDto(
    val selectedCategories: List<String> = emptyList(),
    val quizCompleted: Boolean = false
)

private fun UserPreferencesDto.toDomain() = UserPreferences(
    selectedCategories = selectedCategories,
    onboardingCompleted = false,
    quizCompleted = quizCompleted
)
