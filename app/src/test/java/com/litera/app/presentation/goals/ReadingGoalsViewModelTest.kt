package com.litera.app.presentation.goals

import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ReadingGoalType
import com.litera.app.domain.usecase.DeleteReadingGoalUseCase
import com.litera.app.domain.usecase.ObserveReadingGoalsUseCase
import com.litera.app.domain.usecase.SaveReadingGoalUseCase
import com.litera.app.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReadingGoalsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeReadingGoalsUseCase: ObserveReadingGoalsUseCase = mockk()
    private val saveReadingGoalUseCase: SaveReadingGoalUseCase = mockk(relaxed = true)
    private val deleteReadingGoalUseCase: DeleteReadingGoalUseCase = mockk(relaxed = true)

    private lateinit var goalsFlow: MutableStateFlow<List<ReadingGoal>>
    private lateinit var viewModel: ReadingGoalsViewModel

    private fun existingGoal() = ReadingGoal(
        id = 1L,
        label = "Ler mais",
        type = ReadingGoalType.BOOKS_PER_MONTH,
        targetValue = 2,
        progressValue = 1,
        createdAt = 500L
    )

    private fun createViewModel(initial: List<ReadingGoal> = emptyList()) {
        goalsFlow = MutableStateFlow(initial)
        every { observeReadingGoalsUseCase() } returns goalsFlow
        viewModel = ReadingGoalsViewModel(observeReadingGoalsUseCase, saveReadingGoalUseCase, deleteReadingGoalUseCase)
    }

    @Test
    fun `loads goals from the use case on init`() = runTest {
        createViewModel(listOf(existingGoal()))

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(listOf(existingGoal()), state.goals)
    }

    @Test
    fun `openCreateDialog clears editingGoal and shows the dialog`() = runTest {
        createViewModel()

        viewModel.openCreateDialog()

        val state = viewModel.uiState.value
        assertTrue(state.showDialog)
        assertNull(state.editingGoal)
    }

    @Test
    fun `openEditDialog preloads the goal being edited`() = runTest {
        createViewModel()
        val goal = existingGoal()

        viewModel.openEditDialog(goal)

        val state = viewModel.uiState.value
        assertTrue(state.showDialog)
        assertEquals(goal, state.editingGoal)
    }

    @Test
    fun `dismissDialog hides the dialog and clears editingGoal`() = runTest {
        createViewModel()
        viewModel.openEditDialog(existingGoal())

        viewModel.dismissDialog()

        val state = viewModel.uiState.value
        assertFalse(state.showDialog)
        assertNull(state.editingGoal)
    }

    @Test
    fun `saveGoal creates a new goal with zero progress when not editing`() = runTest {
        createViewModel()
        val savedGoal = slot<ReadingGoal>()
        coEvery { saveReadingGoalUseCase(capture(savedGoal)) } returns 10L

        viewModel.saveGoal("Ler mais", ReadingGoalType.PAGES_PER_WEEK, 100)

        assertEquals(0L, savedGoal.captured.id)
        assertEquals("Ler mais", savedGoal.captured.label)
        assertEquals(ReadingGoalType.PAGES_PER_WEEK, savedGoal.captured.type)
        assertEquals(100, savedGoal.captured.targetValue)
        assertEquals(0, savedGoal.captured.progressValue)
        assertFalse(viewModel.uiState.value.showDialog)
    }

    @Test
    fun `saveGoal preserves id, progress and createdAt when editing an existing goal`() = runTest {
        createViewModel()
        val editing = existingGoal()
        viewModel.openEditDialog(editing)
        val savedGoal = slot<ReadingGoal>()
        coEvery { saveReadingGoalUseCase(capture(savedGoal)) } returns editing.id

        viewModel.saveGoal("Ler mais ainda", ReadingGoalType.BOOKS_PER_MONTH, 5)

        assertEquals(editing.id, savedGoal.captured.id)
        assertEquals(editing.progressValue, savedGoal.captured.progressValue)
        assertEquals(editing.createdAt, savedGoal.captured.createdAt)
        assertEquals(5, savedGoal.captured.targetValue)
    }

    @Test
    fun `deleteGoal delegates to the use case`() = runTest {
        createViewModel()

        viewModel.deleteGoal(1L)

        coVerify(exactly = 1) { deleteReadingGoalUseCase(1L) }
    }
}
