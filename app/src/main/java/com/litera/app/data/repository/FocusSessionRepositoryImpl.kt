@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.litera.app.data.remote.currentUserIdFlow
import com.litera.app.data.remote.safeWrite
import com.litera.app.data.remote.snapshotsFlow
import com.litera.app.data.remote.userCollection
import com.litera.app.domain.model.FocusSession
import com.litera.app.domain.model.FocusStats
import com.litera.app.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION = "focusSessions"
private const val XP_PER_SESSION = 10

class FocusSessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : FocusSessionRepository {

    private fun sessionsCollection(uid: String) = firestore.userCollection(uid, COLLECTION)

    private fun observeAll(): Flow<List<FocusSession>> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                sessionsCollection(uid).orderBy("completedAt", Query.Direction.DESCENDING).snapshotsFlow()
                    .map { snapshot -> snapshot.documents.mapNotNull { it.toObject(FocusSessionDto::class.java)?.toDomain(it.id) } }
            }
        }

    override fun observeSessions(): Flow<List<FocusSession>> = observeAll()

    override fun observeStats(): Flow<FocusStats> = observeAll().map { list ->
        FocusStats(
            totalFocusSeconds = list.sumOf { it.durationSeconds.toLong() },
            sessionsCompleted = list.size,
            xp = list.size * XP_PER_SESSION
        )
    }

    override suspend fun recordSession(durationSeconds: Int) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        val id = System.currentTimeMillis()
        sessionsCollection(uid).document(id.toString()).set(
            FocusSessionDto(durationSeconds = durationSeconds, completedAt = id)
        ).await()
    }
}

data class FocusSessionDto(
    val durationSeconds: Int = 0,
    val completedAt: Long = 0L
)

private fun FocusSessionDto.toDomain(docId: String) = FocusSession(
    id = docId.toLongOrNull() ?: 0L,
    durationSeconds = durationSeconds,
    completedAt = completedAt
)
