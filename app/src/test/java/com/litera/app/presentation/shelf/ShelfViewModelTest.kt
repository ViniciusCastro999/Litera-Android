package com.litera.app.presentation.shelf

import app.cash.turbine.test
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.usecase.ObserveShelfUseCase
import com.litera.app.domain.usecase.RemoveFromShelfUseCase
import com.litera.app.domain.usecase.UpdateReadingProgressUseCase
import com.litera.app.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ShelfViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeShelfUseCase: ObserveShelfUseCase = mockk()
    private val updateReadingProgressUseCase: UpdateReadingProgressUseCase = mockk(relaxed = true)
    private val removeFromShelfUseCase: RemoveFromShelfUseCase = mockk(relaxed = true)

    private lateinit var shelfFlow: MutableStateFlow<List<ShelfBook>>
    private lateinit var viewModel: ShelfViewModel

    private fun book(id: String, isFavorite: Boolean = false, isRead: Boolean = false, currentPage: Int = 0, updatedAt: Long = 0L) =
        ShelfBook(
            volumeId = id,
            title = "Livro $id",
            authorsLabel = "Autor",
            thumbnailUrl = null,
            isFavorite = isFavorite,
            isRead = isRead,
            currentPage = currentPage,
            totalPages = 100,
            addedAt = 0L,
            updatedAt = updatedAt
        )

    private fun createViewModel(initial: List<ShelfBook> = emptyList()) {
        shelfFlow = MutableStateFlow(initial)
        every { observeShelfUseCase() } returns shelfFlow
        viewModel = ShelfViewModel(observeShelfUseCase, updateReadingProgressUseCase, removeFromShelfUseCase)
    }

    @Test
    fun `splits shelf items into reading, favorites and read buckets`() = runTest {
        createViewModel(
            listOf(
                book("reading", currentPage = 10, updatedAt = 1L),
                book("favorite", isFavorite = true),
                book("done", isRead = true, updatedAt = 2L)
            )
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(listOf("reading"), state.currentlyReading.map { it.volumeId })
            assertEquals(listOf("favorite"), state.favorites.map { it.volumeId })
            assertEquals(listOf("done"), state.read.map { it.volumeId })
        }
    }

    @Test
    fun `currentlyReading and read lists are sorted by most recently updated first`() = runTest {
        createViewModel(
            listOf(
                book("older", currentPage = 5, updatedAt = 1L),
                book("newer", currentPage = 5, updatedAt = 2L)
            )
        )

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(listOf("newer", "older"), state.currentlyReading.map { it.volumeId })
        }
    }

    @Test
    fun `startEditingProgress and dismissProgressDialog control editingItem`() = runTest {
        val item = book("1")
        createViewModel(listOf(item))

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.startEditingProgress(item)
            assertEquals(item, awaitItem().editingItem)

            viewModel.dismissProgressDialog()
            assertNull(awaitItem().editingItem)
        }
    }

    @Test
    fun `updateProgress delegates to the use case and dismisses the dialog`() = runTest {
        val item = book("1")
        createViewModel(listOf(item))
        coEvery { updateReadingProgressUseCase("1", 42, 100) } returns Unit
        viewModel.startEditingProgress(item)

        viewModel.updateProgress(42, 100)

        coVerify(exactly = 1) { updateReadingProgressUseCase("1", 42, 100) }
        assertNull(viewModel.uiState.value.editingItem)
    }

    @Test
    fun `updateProgress does nothing when there is no item being edited`() = runTest {
        createViewModel(emptyList())

        viewModel.updateProgress(1, 10)

        coVerify(exactly = 0) { updateReadingProgressUseCase(any(), any(), any()) }
    }

    @Test
    fun `removeFromShelf delegates to the use case`() = runTest {
        createViewModel(emptyList())

        viewModel.removeFromShelf("1")

        coVerify(exactly = 1) { removeFromShelfUseCase("1") }
    }
}
