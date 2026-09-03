@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.litera.app.data.remote.currentUserIdFlow
import com.litera.app.data.remote.safeWrite
import com.litera.app.data.remote.snapshotsFlow
import com.litera.app.data.remote.userCollection
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ReadingGoalType
import com.litera.app.domain.repository.ReadingGoalRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

private const val COLLECTION = "goals"
private const val WRITE_TIMEOUT_MS = 15_000L

class ReadingGoalRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ReadingGoalRepository {

    private fun goalsCollection(uid: String) = firestore.userCollection(uid, COLLECTION)

    override fun observeGoals(): Flow<List<ReadingGoal>> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                goalsCollection(uid).orderBy("createdAt", Query.Direction.DESCENDING).snapshotsFlow()
                    .map { snapshot -> snapshot.documents.mapNotNull { it.toObject(ReadingGoalDto::class.java)?.toDomain(it.id) } }
            }
        }

    override suspend fun getGoal(id: Long): ReadingGoal? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            withTimeout(WRITE_TIMEOUT_MS) {
                goalsCollection(uid).document(id.toString()).get().await()
                    .toObject(ReadingGoalDto::class.java)?.toDomain(id.toString())
            }
        } catch (e: TimeoutCancellationException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    override suspend fun saveGoal(goal: ReadingGoal): Long {
        val uid = auth.currentUser?.uid ?: return goal.id
        val resolvedId = if (goal.id != 0L) goal.id else System.currentTimeMillis()
        try {
            withTimeout(WRITE_TIMEOUT_MS) {
                goalsCollection(uid).document(resolvedId.toString()).set(goal.copy(id = resolvedId).toDto()).await()
            }
        } catch (e: TimeoutCancellationException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
        return resolvedId
    }

    override suspend fun deleteGoal(id: Long) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        goalsCollection(uid).document(id.toString()).delete().await()
    }
}

data class ReadingGoalDto(
    val label: String = "",
    val type: String = ReadingGoalType.PAGES_PER_WEEK.name,
    val targetValue: Int = 0,
    val progressValue: Int = 0,
    val createdAt: Long = 0L
)

private fun ReadingGoalDto.toDomain(docId: String) = ReadingGoal(
    id = docId.toLongOrNull() ?: 0L,
    label = label,
    type = runCatching { ReadingGoalType.valueOf(type) }.getOrDefault(ReadingGoalType.PAGES_PER_WEEK),
    targetValue = targetValue,
    progressValue = progressValue,
    createdAt = createdAt
)

private fun ReadingGoal.toDto() = ReadingGoalDto(
    label = label,
    type = type.name,
    targetValue = targetValue,
    progressValue = progressValue,
    createdAt = if (createdAt > 0) createdAt else System.currentTimeMillis()
)
