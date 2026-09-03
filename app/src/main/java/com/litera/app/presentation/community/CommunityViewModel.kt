package com.litera.app.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.model.Club
import com.litera.app.domain.model.Post
import com.litera.app.domain.usecase.ObserveClubsUseCase
import com.litera.app.domain.usecase.ObservePostsUseCase
import com.litera.app.domain.usecase.SetClubMembershipUseCase
import com.litera.app.domain.usecase.ToggleLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CommunityTab {
    Amigos, Clubes
}

data class CommunityUiState(
    val tab: CommunityTab = CommunityTab.Amigos,
    val isLoading: Boolean = true,
    val posts: List<Post> = emptyList(),
    val clubs: List<Club> = emptyList()
)

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val observePostsUseCase: ObservePostsUseCase,
    private val observeClubsUseCase: ObserveClubsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val setClubMembershipUseCase: SetClubMembershipUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        observePosts()
        observeClubs()
    }

    private fun observePosts() {
        viewModelScope.launch {
            observePostsUseCase().collect { posts ->
                _uiState.value = _uiState.value.copy(posts = posts, isLoading = false)
            }
        }
    }

    private fun observeClubs() {
        viewModelScope.launch {
            observeClubsUseCase().collect { clubs ->
                _uiState.value = _uiState.value.copy(clubs = clubs)
            }
        }
    }

    fun selectTab(tab: CommunityTab) {
        _uiState.value = _uiState.value.copy(tab = tab)
    }

    fun toggleLike(postId: Long) {
        viewModelScope.launch { toggleLikeUseCase(postId) }
    }

    fun toggleClubMembership(club: Club) {
        viewModelScope.launch { setClubMembershipUseCase(club.id, !club.isMember) }
    }
}
