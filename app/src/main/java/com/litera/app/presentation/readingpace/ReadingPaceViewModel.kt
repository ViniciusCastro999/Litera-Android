package com.litera.app.presentation.readingpace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.usecase.SaveLastPagesPerHourUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.math.ceil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReadingPaceUiState(
    val elapsedSeconds: Int = 0,
    val isRunning: Boolean = false,
    val totalPagesText: String = "",
    val startPageText: String = "",
    val endPageText: String = "",
    val desiredDaysText: String = "",
    val pagesPerHour: Int = 0,
    val estimatedDays: Int = 0
)

/**
 * Shared across all four "Ritmo de leitura" screens (intro, timer,
 * calculate, result) via a nested Navigation Compose graph scoped to that
 * flow, so the stopwatch reading + page inputs survive screen-to-screen
 * navigation without persisting them anywhere.
 */
@HiltViewModel
class ReadingPaceViewModel @Inject constructor(
    private val saveLastPagesPerHourUseCase: SaveLastPagesPerHourUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingPaceUiState())
    val uiState: StateFlow<ReadingPaceUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun toggleTimer() {
        if (_uiState.value.isRunning) pauseTimer() else startTimer()
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(elapsedSeconds = 0, isRunning = false)
    }

    private fun startTimer() {
        _uiState.value = _uiState.value.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.value = _uiState.value.copy(elapsedSeconds = _uiState.value.elapsedSeconds + 1)
            }
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun updateTotalPages(text: String) {
        _uiState.value = _uiState.value.copy(totalPagesText = text.filter { it.isDigit() })
    }

    fun updateStartPage(text: String) {
        _uiState.value = _uiState.value.copy(startPageText = text.filter { it.isDigit() })
    }

    fun updateEndPage(text: String) {
        _uiState.value = _uiState.value.copy(endPageText = text.filter { it.isDigit() })
    }

    fun updateDesiredDays(text: String) {
        _uiState.value = _uiState.value.copy(desiredDaysText = text.filter { it.isDigit() })
    }

    /** Computes pages/hour + an estimated finish time, then persists the pace for reuse elsewhere. */
    fun calculateResult() {
        pauseTimer()
        val state = _uiState.value
        val startPage = state.startPageText.toIntOrNull() ?: 0
        val endPage = state.endPageText.toIntOrNull() ?: 0
        val totalPages = state.totalPagesText.toIntOrNull() ?: 0
        val pagesRead = (endPage - startPage).coerceAtLeast(0)
        val elapsedHours = state.elapsedSeconds / 3600.0
        val pagesPerHour = if (elapsedHours > 0) (pagesRead / elapsedHours).toInt() else 0
        val remainingPages = (totalPages - endPage).coerceAtLeast(0)
        val estimatedDays = if (pagesPerHour > 0) ceil(remainingPages / pagesPerHour.toDouble()).toInt() else 0

        _uiState.value = state.copy(pagesPerHour = pagesPerHour, estimatedDays = estimatedDays)

        if (pagesPerHour > 0) {
            viewModelScope.launch { saveLastPagesPerHourUseCase(pagesPerHour) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
