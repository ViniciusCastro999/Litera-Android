package com.litera.app.presentation.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.Club
import com.litera.app.domain.usecase.ObserveClubUseCase
import com.litera.app.domain.usecase.SetClubMembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClubDetailUiState(
    val isLoading: Boolean = true,
    val club: Club? = null
)

@HiltViewModel
class ClubDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeClubUseCase: ObserveClubUseCase,
    private val setClubMembershipUseCase: SetClubMembershipUseCase
) : ViewModel() {

    private val clubId: Long = checkNotNull(savedStateHandle[Screen.ClubDetail.ARG_CLUB_ID])

    private val _uiState = MutableStateFlow(ClubDetailUiState())
    val uiState: StateFlow<ClubDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeClubUseCase(clubId).collect { club ->
                _uiState.value = _uiState.value.copy(club = club, isLoading = false)
            }
        }
    }

    fun toggleMembership() {
        val club = _uiState.value.club ?: return
        viewModelScope.launch { setClubMembershipUseCase(club.id, !club.isMember) }
    }
}
