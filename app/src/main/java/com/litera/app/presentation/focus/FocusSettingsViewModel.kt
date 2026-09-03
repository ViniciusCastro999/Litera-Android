package com.litera.app.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.model.FocusSettings
import com.litera.app.domain.model.FocusStats
import com.litera.app.domain.usecase.ObserveFocusSettingsUseCase
import com.litera.app.domain.usecase.ObserveFocusStatsUseCase
import com.litera.app.domain.usecase.ObserveShelfUseCase
import com.litera.app.domain.usecase.SetFocusDurationUseCase
import com.litera.app.domain.usecase.SetFocusNotificationPrefsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FocusSettingsUiState(
    val isLoading: Boolean = true,
    val settings: FocusSettings = FocusSettings(),
    val stats: FocusStats = FocusStats(0, 0, 0),
    val booksReadCount: Int = 0
)

@HiltViewModel
class FocusSettingsViewModel @Inject constructor(
    private val observeFocusSettingsUseCase: ObserveFocusSettingsUseCase,
    private val observeFocusStatsUseCase: ObserveFocusStatsUseCase,
    private val observeShelfUseCase: ObserveShelfUseCase,
    private val setFocusDurationUseCase: SetFocusDurationUseCase,
    private val setFocusNotificationPrefsUseCase: SetFocusNotificationPrefsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusSettingsUiState())
    val uiState: StateFlow<FocusSettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observeFocusSettingsUseCase(),
                observeFocusStatsUseCase(),
                observeShelfUseCase()
            ) { settings, stats, shelf ->
                FocusSettingsUiState(
                    isLoading = false,
                    settings = settings,
                    stats = stats,
                    booksReadCount = shelf.count { it.isRead }
                )
            }.collect { _uiState.value = it }
        }
    }

    fun selectDuration(seconds: Int) {
        viewModelScope.launch { setFocusDurationUseCase(seconds) }
    }

    fun setNotificationPrefs(morning: Boolean, afternoon: Boolean, night: Boolean) {
        viewModelScope.launch { setFocusNotificationPrefsUseCase(morning, afternoon, night) }
    }
}
