package com.litera.app.data.repository

import com.litera.app.data.local.dao.NoteDao
import com.litera.app.data.local.entity.NoteEntity
import com.litera.app.domain.model.Note
import com.litera.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG_DELIMITER = "||"

class NoteRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NoteRepository {

    override fun observeNotes(volumeId: String): Flow<List<Note>> =
        dao.observeByVolume(volumeId).map { list -> list.map { it.toDomain() } }

    override suspend fun saveNote(volumeId: String, text: String, tags: List<String>) {
        dao.upsert(
            NoteEntity(
                volumeId = volumeId,
                text = text,
                tags = tags.joinToString(TAG_DELIMITER),
                createdAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun deleteNote(note: Note) {
        dao.delete(
            NoteEntity(
                id = note.id,
                volumeId = note.volumeId,
                text = note.text,
                tags = note.tags.joinToString(TAG_DELIMITER),
                createdAt = note.createdAt
            )
        )
    }
}

private fun NoteEntity.toDomain() = Note(
    id = id,
    volumeId = volumeId,
    text = text,
    tags = tags.split(TAG_DELIMITER).filter { it.isNotBlank() },
    createdAt = createdAt
)
