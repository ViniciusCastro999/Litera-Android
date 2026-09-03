package com.litera.app.domain.usecase

import com.litera.app.domain.model.Note
import com.litera.app.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    operator fun invoke(volumeId: String): Flow<List<Note>> = repository.observeNotes(volumeId)
}

class SaveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(volumeId: String, text: String, tags: List<String>) =
        repository.saveNote(volumeId, text, tags)
}

class DeleteNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) = repository.deleteNote(note)
}
