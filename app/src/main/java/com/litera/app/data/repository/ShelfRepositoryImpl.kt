@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.litera.app.data.remote.currentUserIdFlow
import com.litera.app.data.remote.safeWrite
import com.litera.app.data.remote.snapshotsFlow
import com.litera.app.data.remote.userCollection
import com.litera.app.domain.model.Book
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.repository.ShelfRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION = "shelf"

/**
 * Firestore-backed shelf, under users/{uid}/shelf/{volumeId} — replaces the
 * old Room table so "Estante" follows the signed-in account across devices
 * instead of staying on a single phone.
 *
 * Writes are best-effort: if Firestore isn't reachable/provisioned yet, the
 * exception is recorded to Crashlytics and swallowed rather than crashing
 * the tapped screen (ShelfViewModel's action methods are fire-and-forget,
 * same as they were with Room, which practically never failed).
 */
class ShelfRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ShelfRepository {

    private fun shelfCollection(uid: String) = firestore.userCollection(uid, COLLECTION)

    override fun observeShelf(): Flow<List<ShelfBook>> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                shelfCollection(uid).orderBy("updatedAt", Query.Direction.DESCENDING).snapshotsFlow()
                    .map { snapshot -> snapshot.documents.mapNotNull { it.toObject(ShelfBookDto::class.java)?.toDomain(it.id) } }
            }
        }

    override fun observeShelfItem(volumeId: String): Flow<ShelfBook?> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(null)
            } else {
                shelfCollection(uid).document(volumeId).snapshotsFlow()
                    .map { it.toObject(ShelfBookDto::class.java)?.toDomain(it.id) }
            }
        }

    override suspend fun toggleFavorite(book: Book) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        val docRef = shelfCollection(uid).document(book.volumeId)
        val existing = docRef.get().await().toObject(ShelfBookDto::class.java)
        val now = System.currentTimeMillis()
        if (existing != null) {
            docRef.set(existing.copy(favorite = !existing.favorite, updatedAt = now)).await()
        } else {
            docRef.set(book.toNewDto(now).copy(favorite = true)).await()
        }
    }

    override suspend fun startReading(book: Book, totalPages: Int) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        val docRef = shelfCollection(uid).document(book.volumeId)
        val existing = docRef.get().await().toObject(ShelfBookDto::class.java)
        val now = System.currentTimeMillis()
        if (existing != null) {
            docRef.set(
                existing.copy(
                    read = false,
                    currentPage = if (existing.currentPage <= 0) 1 else existing.currentPage,
                    totalPages = if (totalPages > 0) totalPages else existing.totalPages,
                    updatedAt = now
                )
            ).await()
        } else {
            docRef.set(book.toNewDto(now).copy(currentPage = 1, totalPages = totalPages)).await()
        }
    }

    override suspend fun updateProgress(volumeId: String, currentPage: Int, totalPages: Int) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        val docRef = shelfCollection(uid).document(volumeId)
        val existing = docRef.get().await().toObject(ShelfBookDto::class.java) ?: return@safeWrite
        val now = System.currentTimeMillis()
        val isNowRead = totalPages > 0 && currentPage >= totalPages
        docRef.set(
            existing.copy(
                currentPage = currentPage.coerceAtLeast(0),
                totalPages = totalPages.coerceAtLeast(existing.totalPages),
                read = existing.read || isNowRead,
                updatedAt = now
            )
        ).await()
    }

    override suspend fun markAsRead(volumeId: String) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        val docRef = shelfCollection(uid).document(volumeId)
        val existing = docRef.get().await().toObject(ShelfBookDto::class.java) ?: return@safeWrite
        docRef.set(existing.copy(read = true, updatedAt = System.currentTimeMillis())).await()
    }

    override suspend fun removeFromShelf(volumeId: String) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        shelfCollection(uid).document(volumeId).delete().await()
    }
}

/**
 * Firestore requires a no-arg constructor for automatic deserialization,
 * hence every field defaults. Booleans are named without an "is" prefix on
 * purpose: Kotlin compiles `val isFavorite: Boolean` to a getter literally
 * named `isFavorite()`, and Firestore's bean-property mapper strips "is" to
 * derive the serialized field name ("favorite") but then looks for a field/
 * setter matching that SAME derived name when deserializing — which doesn't
 * exist (the real field is `isFavorite`), so it silently drops the value.
 * Plain `favorite`/`read` sidesteps this mismatch entirely.
 */
data class ShelfBookDto(
    val title: String = "",
    val authorsLabel: String = "",
    val thumbnailUrl: String? = null,
    val favorite: Boolean = false,
    val read: Boolean = false,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val addedAt: Long = 0L,
    val updatedAt: Long = 0L
)

private fun ShelfBookDto.toDomain(volumeId: String) = ShelfBook(
    volumeId = volumeId,
    title = title,
    authorsLabel = authorsLabel,
    thumbnailUrl = thumbnailUrl,
    isFavorite = favorite,
    isRead = read,
    currentPage = currentPage,
    totalPages = totalPages,
    addedAt = addedAt,
    updatedAt = updatedAt
)

private fun Book.toNewDto(now: Long) = ShelfBookDto(
    title = title,
    authorsLabel = authorsLabel,
    thumbnailUrl = thumbnailUrl,
    favorite = false,
    read = false,
    currentPage = 0,
    totalPages = pageCount,
    addedAt = now,
    updatedAt = now
)
