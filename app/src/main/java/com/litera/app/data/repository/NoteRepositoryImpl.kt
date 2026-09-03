@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package com.litera.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.litera.app.data.remote.currentUserIdFlow
import com.litera.app.data.remote.safeWrite
import com.litera.app.data.remote.snapshotsFlow
import com.litera.app.data.remote.userCollection
import com.litera.app.domain.model.Note
import com.litera.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val COLLECTION = "notes"
private const val ITEMS = "items"

/**
 * Notes live at users/{uid}/notes/{volumeId}/items/{noteId} — nesting by
 * volume (instead of a flat collection filtered by a volumeId field) means
 * "notes for this book, newest first" is a plain single-field orderBy with
 * no composite index to provision in Firestore.
 */
class NoteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : NoteRepository {

    private fun notesForVolume(uid: String, volumeId: String) =
        firestore.userCollection(uid, COLLECTION).document(volumeId).collection(ITEMS)

    override fun observeNotes(volumeId: String): Flow<List<Note>> =
        auth.currentUserIdFlow().flatMapLatest { uid ->
            if (uid == null) {
                flowOf(emptyList())
            } else {
                notesForVolume(uid, volumeId).orderBy("createdAt", Query.Direction.DESCENDING).snapshotsFlow()
                    .map { snapshot -> snapshot.documents.mapNotNull { it.toObject(NoteDto::class.java)?.toDomain(it.id, volumeId) } }
            }
        }

    override suspend fun saveNote(volumeId: String, text: String, tags: List<String>) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        val id = System.currentTimeMillis()
        notesForVolume(uid, volumeId).document(id.toString()).set(
            NoteDto(text = text, tags = tags, createdAt = id)
        ).await()
    }

    override suspend fun deleteNote(note: Note) = safeWrite {
        val uid = auth.currentUser?.uid ?: return@safeWrite
        notesForVolume(uid, note.volumeId).document(note.id.toString()).delete().await()
    }
}

data class NoteDto(
    val text: String = "",
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0L
)

private fun NoteDto.toDomain(docId: String, volumeId: String) = Note(
    id = docId.toLongOrNull() ?: 0L,
    volumeId = volumeId,
    text = text,
    tags = tags,
    createdAt = createdAt
)
