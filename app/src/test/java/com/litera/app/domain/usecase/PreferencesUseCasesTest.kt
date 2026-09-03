package com.litera.app.domain.usecase

import com.litera.app.core.common.Resource
import com.litera.app.domain.repository.PreferencesRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PreferencesUseCasesTest {

    private val repository: PreferencesRepository = mockk(relaxed = true)
    private lateinit var saveSelectedCategories: SaveSelectedCategoriesUseCase

    @Before
    fun setUp() {
        saveSelectedCategories = SaveSelectedCategoriesUseCase(repository)
    }

    @Test
    fun `rejects fewer than the minimum required categories`() = runTest {
        val result = saveSelectedCategories(listOf("Fantasia", "Romance"))

        assertTrue(result is Resource.Error)
        coVerify(exactly = 0) { repository.saveSelectedCategories(any()) }
        coVerify(exactly = 0) { repository.setQuizCompleted(any()) }
    }

    @Test
    fun `saves categories and marks quiz completed when at minimum`() = runTest {
        val categories = listOf("Fantasia", "Romance", "Terror")

        val result = saveSelectedCategories(categories)

        assertTrue(result is Resource.Success)
        coVerify(exactly = 1) { repository.saveSelectedCategories(categories) }
        coVerify(exactly = 1) { repository.setQuizCompleted(true) }
    }
}
