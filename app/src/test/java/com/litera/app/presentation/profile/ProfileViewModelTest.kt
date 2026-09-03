package com.litera.app.presentation.profile

import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ReadingGoalType
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.model.UserPreferences
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.ObservePreferencesUseCase
import com.litera.app.domain.usecase.ObserveReadingGoalsUseCase
import com.litera.app.domain.usecase.ObserveShelfUseCase
import com.litera.app.domain.usecase.SignOutUseCase
import com.litera.app.testutil.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeCurrentUser: ObserveCurrentUserUseCase = mockk()
    private val observeShelfUseCase: ObserveShelfUseCase = mockk()
    private val observePreferencesUseCase: ObservePreferencesUseCase = mockk()
    private val observeReadingGoalsUseCase: ObserveReadingGoalsUseCase = mockk()
    private val signOutUseCase: SignOutUseCase = mockk(relaxed = true)

    private fun shelfBook(isRead: Boolean = false, isFavorite: Boolean = false, currentPage: Int = 0) = ShelfBook(
        volumeId = "1",
        title = "Livro",
        authorsLabel = "Autor",
        thumbnailUrl = null,
        isFavorite = isFavorite,
        isRead = isRead,
        currentPage = currentPage,
        totalPages = 100,
        addedAt = 0L,
        updatedAt = 0L
    )

    private fun createViewModel(
        user: AuthUser? = AuthUser(uid = "1", email = "reader@litera.com", displayName = "Reader"),
        shelf: List<ShelfBook> = emptyList(),
        preferences: UserPreferences = UserPreferences(),
        goals: List<ReadingGoal> = emptyList()
    ): ProfileViewModel {
        every { observeCurrentUser() } returns MutableStateFlow(user)
        every { observeShelfUseCase() } returns MutableStateFlow(shelf)
        every { observePreferencesUseCase() } returns MutableStateFlow(preferences)
        every { observeReadingGoalsUseCase() } returns MutableStateFlow(goals)
        return ProfileViewModel(observeCurrentUser, observeShelfUseCase, observePreferencesUseCase, observeReadingGoalsUseCase, signOutUseCase)
    }

    @Test
    fun `combines user, shelf counts, favorite categories and top goal into uiState`() = runTest {
        val goal = ReadingGoal(1L, "Meta", ReadingGoalType.BOOKS_PER_MONTH, 5, 2, 0L)
        val viewModel = createViewModel(
            shelf = listOf(
                shelfBook(isRead = true),
                shelfBook(currentPage = 5),
                shelfBook(isFavorite = true)
            ),
            preferences = UserPreferences(selectedCategories = listOf("Fantasia", "Romance")),
            goals = listOf(goal)
        )

        val state = viewModel.uiState.value
        assertEquals("reader@litera.com", state.user?.email)
        assertEquals(1, state.booksReadCount)
        assertEquals(1, state.booksReadingCount)
        assertEquals(1, state.favoriteCount)
        assertEquals(listOf("Fantasia", "Romance"), state.favoriteCategories)
        assertEquals(goal, state.topGoal)
    }

    @Test
    fun `topGoal is null when there are no goals`() = runTest {
        val viewModel = createViewModel(goals = emptyList())

        assertNull(viewModel.uiState.value.topGoal)
    }

    @Test
    fun `signOut delegates to the use case`() = runTest {
        val viewModel = createViewModel()

        viewModel.signOut()

        verify(exactly = 1) { signOutUseCase() }
    }
}
