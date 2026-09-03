package com.litera.app.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.model.FocusSession
import com.litera.app.domain.model.FocusStats
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.model.ShelfBook
import com.litera.app.domain.usecase.ObserveFocusSessionsUseCase
import com.litera.app.domain.usecase.ObserveFocusStatsUseCase
import com.litera.app.domain.usecase.ObserveReadingGoalsUseCase
import com.litera.app.domain.usecase.ObserveShelfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/** Which fixed achievement badge this is; the screen maps this to an icon/label. */
enum class AchievementBadge(val title: String) {
    FIRST_BOOK_READ("Primeiro livro lido"),
    FIVE_BOOKS_READ("5 livros lidos"),
    FOCUSED_60_MIN("Focou por 60 min"),
    TEN_FOCUS_SESSIONS("10 sessões de foco"),
    FIRST_GOAL_CREATED("Primeira meta criada")
}

data class AchievementUiModel(
    val badge: AchievementBadge,
    val unlocked: Boolean
)

data class FocusSessionUiModel(
    val id: Long,
    val dateLabel: String,
    val minutesLabel: String
)

data class ReadingProgressUiState(
    val isLoading: Boolean = true,
    val pagesRead: Int = 0,
    val pagesTotal: Int = 0,
    val progressPercent: Int = 0,
    val achievements: List<AchievementUiModel> = emptyList(),
    val recentSessions: List<FocusSessionUiModel> = emptyList()
)

private val sessionDateFormat = SimpleDateFormat("dd/MM", Locale("pt", "BR"))

@HiltViewModel
class ReadingProgressViewModel @Inject constructor(
    observeShelfUseCase: ObserveShelfUseCase,
    observeFocusStatsUseCase: ObserveFocusStatsUseCase,
    observeFocusSessionsUseCase: ObserveFocusSessionsUseCase,
    observeReadingGoalsUseCase: ObserveReadingGoalsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingProgressUiState())
    val uiState: StateFlow<ReadingProgressUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observeShelfUseCase(),
                observeFocusStatsUseCase(),
                observeFocusSessionsUseCase(),
                observeReadingGoalsUseCase()
            ) { shelf, focusStats, focusSessions, goals ->
                buildUiState(shelf, focusStats, focusSessions, goals)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun buildUiState(
        shelf: List<ShelfBook>,
        focusStats: FocusStats,
        focusSessions: List<FocusSession>,
        goals: List<ReadingGoal>
    ): ReadingProgressUiState {
        val currentlyReading = shelf.filter { !it.isRead && it.currentPage > 0 }
        val pagesRead = currentlyReading.sumOf { it.currentPage }
        val pagesTotal = currentlyReading.sumOf { it.totalPages }
        val progressPercent = if (pagesTotal <= 0) 0 else
            ((pagesRead.toFloat() / pagesTotal) * 100f).toInt().coerceIn(0, 100)

        val booksRead = shelf.count { it.isRead }
        val achievements = listOf(
            AchievementUiModel(AchievementBadge.FIRST_BOOK_READ, unlocked = booksRead >= 1),
            AchievementUiModel(AchievementBadge.FIVE_BOOKS_READ, unlocked = booksRead >= 5),
            AchievementUiModel(AchievementBadge.FOCUSED_60_MIN, unlocked = focusStats.totalFocusSeconds >= 3600),
            AchievementUiModel(AchievementBadge.TEN_FOCUS_SESSIONS, unlocked = focusStats.sessionsCompleted >= 10),
            AchievementUiModel(AchievementBadge.FIRST_GOAL_CREATED, unlocked = goals.isNotEmpty())
        )

        val recentSessions = focusSessions
            .sortedByDescending { it.completedAt }
            .take(7)
            .map { session ->
                FocusSessionUiModel(
                    id = session.id,
                    dateLabel = sessionDateFormat.format(java.util.Date(session.completedAt)),
                    minutesLabel = "${session.durationSeconds / 60} min"
                )
            }

        return ReadingProgressUiState(
            isLoading = false,
            pagesRead = pagesRead,
            pagesTotal = pagesTotal,
            progressPercent = progressPercent,
            achievements = achievements,
            recentSessions = recentSessions
        )
    }
}
