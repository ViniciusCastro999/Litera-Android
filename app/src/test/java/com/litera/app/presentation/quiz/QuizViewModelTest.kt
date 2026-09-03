package com.litera.app.presentation.quiz

import com.litera.app.core.common.Resource
import com.litera.app.domain.usecase.SaveSelectedCategoriesUseCase
import com.litera.app.domain.usecase.SetOnboardingCompletedUseCase
import com.litera.app.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuizViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val saveSelectedCategoriesUseCase: SaveSelectedCategoriesUseCase = mockk()
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase = mockk(relaxed = true)

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setUp() {
        viewModel = QuizViewModel(saveSelectedCategoriesUseCase, setOnboardingCompletedUseCase)
    }

    @Test
    fun `toggleCategory selects an unselected category`() {
        viewModel.toggleCategory("Fantasia")

        assertEquals(setOf("Fantasia"), viewModel.uiState.value.selectedCategories)
    }

    @Test
    fun `toggleCategory deselects an already selected category`() {
        viewModel.toggleCategory("Fantasia")

        viewModel.toggleCategory("Fantasia")

        assertTrue(viewModel.uiState.value.selectedCategories.isEmpty())
    }

    @Test
    fun `canSave is false below the minimum category count`() {
        viewModel.toggleCategory("Fantasia")
        viewModel.toggleCategory("Romance")

        assertFalse(viewModel.uiState.value.canSave)
    }

    @Test
    fun `canSave is true at the minimum category count`() {
        viewModel.toggleCategory("Fantasia")
        viewModel.toggleCategory("Romance")
        viewModel.toggleCategory("Terror")

        assertTrue(viewModel.uiState.value.canSave)
    }

    @Test
    fun `save marks onboarding completed and invokes callback on success`() = runTest {
        viewModel.toggleCategory("Fantasia")
        viewModel.toggleCategory("Romance")
        viewModel.toggleCategory("Terror")
        coEvery { saveSelectedCategoriesUseCase(any()) } returns Resource.Success(Unit)
        var saved = false

        viewModel.save { saved = true }

        assertTrue(saved)
        assertFalse(viewModel.uiState.value.isSaving)
        assertNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 1) { setOnboardingCompletedUseCase() }
    }

    @Test
    fun `save surfaces the error message and does not invoke callback on failure`() = runTest {
        coEvery { saveSelectedCategoriesUseCase(any()) } returns Resource.Error("Selecione pelo menos 3 categorias.")
        var saved = false

        viewModel.save { saved = true }

        assertFalse(saved)
        assertFalse(viewModel.uiState.value.isSaving)
        assertEquals("Selecione pelo menos 3 categorias.", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { setOnboardingCompletedUseCase() }
    }

    @Test
    fun `skip marks onboarding completed and invokes callback without saving categories`() = runTest {
        var saved = false

        viewModel.skip { saved = true }

        assertTrue(saved)
        coVerify(exactly = 1) { setOnboardingCompletedUseCase() }
        coVerify(exactly = 0) { saveSelectedCategoriesUseCase(any()) }
    }
}
