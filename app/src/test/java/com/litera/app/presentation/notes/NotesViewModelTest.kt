package com.litera.app.presentation.notes

import androidx.lifecycle.SavedStateHandle
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.Note
import com.litera.app.domain.usecase.DeleteNoteUseCase
import com.litera.app.domain.usecase.ObserveNotesUseCase
import com.litera.app.domain.usecase.SaveNoteUseCase
import com.litera.app.testutil.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeNotesUseCase: ObserveNotesUseCase = mockk()
    private val saveNoteUseCase: SaveNoteUseCase = mockk(relaxed = true)
    private val deleteNoteUseCase: DeleteNoteUseCase = mockk(relaxed = true)

    private lateinit var viewModel: NotesViewModel

    private fun createViewModel(volumeId: String = "vol-1", notes: List<Note> = emptyList()) {
        every { observeNotesUseCase(volumeId) } returns MutableStateFlow(notes)
        val savedStateHandle = SavedStateHandle(mapOf(Screen.Notes.ARG_VOLUME_ID to volumeId))
        viewModel = NotesViewModel(savedStateHandle, observeNotesUseCase, saveNoteUseCase, deleteNoteUseCase)
    }

    @Test
    fun `loads notes for the volume id from the saved state handle`() = runTest {
        val notes = listOf(Note(id = 1L, volumeId = "vol-1", text = "Nota", tags = emptyList(), createdAt = 0L))

        createViewModel(volumeId = "vol-1", notes = notes)

        assertEquals(notes, viewModel.uiState.value.notes)
    }

    @Test
    fun `addTag trims and appends a new tag`() {
        createViewModel()

        viewModel.addTag("  favorito  ")

        assertEquals(listOf("favorito"), viewModel.uiState.value.draftTags)
    }

    @Test
    fun `addTag ignores blank input`() {
        createViewModel()

        viewModel.addTag("   ")

        assertTrue(viewModel.uiState.value.draftTags.isEmpty())
    }

    @Test
    fun `addTag does not add duplicate tags`() {
        createViewModel()

        viewModel.addTag("favorito")
        viewModel.addTag("favorito")

        assertEquals(listOf("favorito"), viewModel.uiState.value.draftTags)
    }

    @Test
    fun `removeTag removes a previously added tag`() {
        createViewModel()
        viewModel.addTag("favorito")

        viewModel.removeTag("favorito")

        assertTrue(viewModel.uiState.value.draftTags.isEmpty())
    }

    @Test
    fun `saveNote does nothing when draft text is blank`() {
        createViewModel()

        viewModel.saveNote()

        coVerify(exactly = 0) { saveNoteUseCase(any(), any(), any()) }
    }

    @Test
    fun `saveNote persists the draft and clears it afterwards`() {
        createViewModel(volumeId = "vol-1")
        viewModel.updateDraftText("Uma nota")
        viewModel.addTag("importante")

        viewModel.saveNote()

        coVerify(exactly = 1) { saveNoteUseCase("vol-1", "Uma nota", listOf("importante")) }
        assertEquals("", viewModel.uiState.value.draftText)
        assertTrue(viewModel.uiState.value.draftTags.isEmpty())
    }

    @Test
    fun `deleteNote delegates to the use case`() {
        createViewModel()
        val note = Note(id = 1L, volumeId = "vol-1", text = "x", tags = emptyList(), createdAt = 0L)

        viewModel.deleteNote(note)

        coVerify(exactly = 1) { deleteNoteUseCase(note) }
    }
}
