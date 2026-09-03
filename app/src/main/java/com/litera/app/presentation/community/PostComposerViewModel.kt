package com.litera.app.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.domain.usecase.CreatePostUseCase
import com.litera.app.domain.usecase.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PostComposerUiState(
    val text: String = "",
    val tags: List<String> = emptyList(),
    val isPosting: Boolean = false,
    val postCreated: Boolean = false
) {
    val canPost: Boolean get() = text.isNotBlank() && !isPosting
}

@HiltViewModel
class PostComposerViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostComposerUiState())
    val uiState: StateFlow<PostComposerUiState> = _uiState.asStateFlow()

    fun updateText(text: String) {
        _uiState.value = _uiState.value.copy(text = text)
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isEmpty()) return
        val current = _uiState.value.tags
        if (current.any { it.equals(trimmed, ignoreCase = true) }) return
        _uiState.value = _uiState.value.copy(tags = current + trimmed)
    }

    fun toggleTag(tag: String) {
        val current = _uiState.value.tags
        _uiState.value = _uiState.value.copy(
            tags = if (current.contains(tag)) current - tag else current + tag
        )
    }

    fun removeTag(tag: String) {
        _uiState.value = _uiState.value.copy(tags = _uiState.value.tags - tag)
    }

    fun createPost() {
        val state = _uiState.value
        if (!state.canPost) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isPosting = true)
            val currentUser = observeCurrentUserUseCase().value
            val authorName = currentUser?.displayName?.let { "@$it" } ?: "@leitor"
            createPostUseCase(
                authorName = authorName,
                authorPhotoUrl = null,
                text = state.text.trim(),
                tags = state.tags
            )
            _uiState.value = _uiState.value.copy(isPosting = false, postCreated = true)
        }
    }
}
