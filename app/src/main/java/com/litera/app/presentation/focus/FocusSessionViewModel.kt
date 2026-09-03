package com.litera.app.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.usecase.ObserveFocusSettingsUseCase
import com.litera.app.domain.usecase.RecordFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusSessionUiState(
    val isLoading: Boolean = true,
    val totalSeconds: Int = 15 * 60,
    val remainingSeconds: Int = 15 * 60,
    val isRunning: Boolean = false,
    val isCompleted: Boolean = false
)

@HiltViewModel
class FocusSessionViewModel @Inject constructor(
    private val observeFocusSettingsUseCase: ObserveFocusSettingsUseCase,
    private val recordFocusSessionUseCase: RecordFocusSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusSessionUiState())
    val uiState: StateFlow<FocusSessionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            val settings = observeFocusSettingsUseCase().first()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                totalSeconds = settings.selectedDurationSeconds,
                remainingSeconds = settings.selectedDurationSeconds
            )
            resume()
        }
    }

    fun toggleRunning() {
        if (_uiState.value.isRunning) pause() else resume()
    }

    fun reset() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            remainingSeconds = _uiState.value.totalSeconds,
            isRunning = false,
            isCompleted = false
        )
    }

    private fun pause() {
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    private fun resume() {
        if (_uiState.value.remainingSeconds <= 0 || _uiState.value.isCompleted) return
        _uiState.value = _uiState.value.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000)
                val next = (_uiState.value.remainingSeconds - 1).coerceAtLeast(0)
                _uiState.value = _uiState.value.copy(remainingSeconds = next)
            }
            _uiState.value = _uiState.value.copy(isRunning = false, isCompleted = true)
            recordFocusSessionUseCase(_uiState.value.totalSeconds)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
