package com.litera.app.presentation.community

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.Comment
import com.litera.app.domain.model.Post
import com.litera.app.domain.usecase.AddCommentUseCase
import com.litera.app.domain.usecase.ObserveCommentsUseCase
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import com.litera.app.domain.usecase.ObservePostUseCase
import com.litera.app.domain.usecase.ToggleLikeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostDetailUiState(
    val isLoading: Boolean = true,
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val commentText: String = ""
)

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observePostUseCase: ObservePostUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val postId: Long = checkNotNull(savedStateHandle[Screen.PostDetail.ARG_POST_ID])

    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    init {
        observePost()
        observeComments()
    }

    private fun observePost() {
        viewModelScope.launch {
            observePostUseCase(postId).collect { post ->
                _uiState.value = _uiState.value.copy(post = post, isLoading = false)
            }
        }
    }

    private fun observeComments() {
        viewModelScope.launch {
            observeCommentsUseCase(postId).collect { comments ->
                _uiState.value = _uiState.value.copy(comments = comments)
            }
        }
    }

    fun toggleLike() {
        viewModelScope.launch { toggleLikeUseCase(postId) }
    }

    fun updateCommentText(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    fun sendComment() {
        val text = _uiState.value.commentText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            val currentUser = observeCurrentUserUseCase().value
            val authorName = currentUser?.displayName?.let { "@$it" } ?: "@leitor"
            addCommentUseCase(postId, authorName, text)
            _uiState.value = _uiState.value.copy(commentText = "")
        }
    }
}
