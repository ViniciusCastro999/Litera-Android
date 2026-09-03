@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.litera.app.data.remote.currentUserIdFlow
import com.litera.app.data.remote.safeWrite
import com.litera.app.data.remote.snapshotsFlow
import com.litera.app.data.remote.userSettingsDoc
import com.litera.app.domain.model.FocusSettings
import com.litera.app.domain.repository.FocusPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val DOC = "focus"

class FocusPreferencesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FocusPreferencesRepository {

    private fun doc(uid: String) = firestore.userSettingsDoc(uid, DOC)

    override fun observeSettings(): Flow<FocusSettings> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(FocusSettings())
            } else {
                doc(uid).snapshotsFlow().map { it.toObject(FocusPreferencesDto::class.java)?.toDomain() ?: FocusSettings() }
            }
        }

    override suspend fun setSelectedDuration(seconds: Int) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        doc(uid).set(mapOf("selectedDurationSeconds" to seconds), SetOptions.merge()).await()
    }

    override suspend fun setNotificationPrefs(morning: Boolean, afternoon: Boolean, night: Boolean) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        doc(uid).set(
            mapOf(
                "notifyMorning" to morning,
                "notifyAfternoon" to afternoon,
                "notifyNight" to night
            ),
            SetOptions.merge()
        ).await()
    }

    override fun observeLastPagesPerHour(): Flow<Int?> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(null)
            } else {
                doc(uid).snapshotsFlow().map { it.getLong("lastPagesPerHour")?.toInt() }
            }
        }

    override suspend fun saveLastPagesPerHour(pagesPerHour: Int) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        doc(uid).set(mapOf("lastPagesPerHour" to pagesPerHour), SetOptions.merge()).await()
    }
}

data class FocusPreferencesDto(
    val selectedDurationSeconds: Int = FocusSettings().selectedDurationSeconds,
    val notifyMorning: Boolean = false,
    val notifyAfternoon: Boolean = false,
    val notifyNight: Boolean = false
)

private fun FocusPreferencesDto.toDomain() = FocusSettings(
    selectedDurationSeconds = selectedDurationSeconds,
    notifyMorning = notifyMorning,
    notifyAfternoon = notifyAfternoon,
    notifyNight = notifyNight
)
