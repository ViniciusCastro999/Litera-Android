package com.litera.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.model.AuthUser
import com.litera.app.domain.model.ReadingGoal
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.ObservePreferencesUseCase
import com.litera.app.domain.usecase.ObserveReadingGoalsUseCase
import com.litera.app.domain.usecase.ObserveShelfUseCase
import com.litera.app.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: AuthUser? = null,
    val booksReadCount: Int = 0,
    val booksReadingCount: Int = 0,
    val favoriteCount: Int = 0,
    val favoriteCategories: List<String> = emptyList(),
    val topGoal: ReadingGoal? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeCurrentUser: ObserveCurrentUserUseCase,
    private val observeShelfUseCase: ObserveShelfUseCase,
    private val observePreferencesUseCase: ObservePreferencesUseCase,
    private val observeReadingGoalsUseCase: ObserveReadingGoalsUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
        viewModelScope.launch {
            observeShelfUseCase().collect { shelf ->
                _uiState.value = _uiState.value.copy(
                    booksReadCount = shelf.count { it.isRead },
                    booksReadingCount = shelf.count { it.isCurrentlyReading },
                    favoriteCount = shelf.count { it.isFavorite }
                )
            }
        }
        viewModelScope.launch {
            observePreferencesUseCase().collect { prefs ->
                _uiState.value = _uiState.value.copy(favoriteCategories = prefs.selectedCategories)
            }
        }
        viewModelScope.launch {
            observeReadingGoalsUseCase().collect { goals ->
                _uiState.value = _uiState.value.copy(topGoal = goals.firstOrNull())
            }
        }
    }

    fun signOut() = signOutUseCase()
}
