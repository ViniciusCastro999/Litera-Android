package com.litera.app.presentation.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.Note
import com.litera.app.domain.usecase.DeleteNoteUseCase
import com.litera.app.domain.usecase.ObserveNotesUseCase
import com.litera.app.domain.usecase.SaveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val isLoading: Boolean = true,
    val notes: List<Note> = emptyList(),
    val draftText: String = "",
    val draftTags: List<String> = emptyList()
)

@HiltViewModel
class NotesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeNotesUseCase: ObserveNotesUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val volumeId: String = java.net.URLDecoder.decode(
        checkNotNull(savedStateHandle[Screen.Notes.ARG_VOLUME_ID]),
        "UTF-8"
    )

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        observeNotes()
    }

    private fun observeNotes() {
        viewModelScope.launch {
            observeNotesUseCase(volumeId).collect { notes ->
                _uiState.value = _uiState.value.copy(isLoading = false, notes = notes)
            }
        }
    }

    fun updateDraftText(text: String) {
        _uiState.value = _uiState.value.copy(draftText = text)
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isEmpty()) return
        val current = _uiState.value.draftTags
        if (current.contains(trimmed)) return
        _uiState.value = _uiState.value.copy(draftTags = current + trimmed)
    }

    fun removeTag(tag: String) {
        _uiState.value = _uiState.value.copy(draftTags = _uiState.value.draftTags - tag)
    }

    fun saveNote() {
        val state = _uiState.value
        if (state.draftText.isBlank()) return
        viewModelScope.launch {
            saveNoteUseCase(volumeId, state.draftText, state.draftTags)
            _uiState.value = _uiState.value.copy(draftText = "", draftTags = emptyList())
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { deleteNoteUseCase(note) }
    }
}
