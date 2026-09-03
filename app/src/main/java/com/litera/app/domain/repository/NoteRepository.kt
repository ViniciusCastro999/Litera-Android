package com.litera.app.domain.repository

import com.litera.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(volumeId: String): Flow<List<Note>>
    suspend fun saveNote(volumeId: String, text: String, tags: List<String>)
    suspend fun deleteNote(note: Note)
}
