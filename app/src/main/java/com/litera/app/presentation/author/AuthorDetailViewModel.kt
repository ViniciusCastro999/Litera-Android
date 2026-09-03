package com.litera.app.presentation.author

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.Resource
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.AuthorProfile
import com.litera.app.domain.model.Book
import com.litera.app.domain.usecase.GetAuthorProfileUseCase
import com.litera.app.domain.usecase.GetBooksByAuthorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthorDetailUiState(
    val isLoading: Boolean = true,
    val profile: AuthorProfile? = null,
    val books: List<Book> = emptyList()
)

@HiltViewModel
class AuthorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAuthorProfileUseCase: GetAuthorProfileUseCase,
    private val getBooksByAuthorUseCase: GetBooksByAuthorUseCase
) : ViewModel() {

    private val authorName: String = java.net.URLDecoder.decode(
        checkNotNull(savedStateHandle[Screen.AuthorDetail.ARG_AUTHOR_NAME]),
        "UTF-8"
    )

    private val _uiState = MutableStateFlow(AuthorDetailUiState())
    val uiState: StateFlow<AuthorDetailUiState> = _uiState.asStateFlow()

    init {
        loadAuthor()
    }

    private fun loadAuthor() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = getAuthorProfileUseCase(authorName)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = result.data
                )
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    profile = AuthorProfile(name = authorName, bio = null, photoUrl = null)
                )
                Resource.Loading -> Unit
            }
            loadBooks()
        }
    }

    private fun loadBooks() {
        viewModelScope.launch {
            val result = getBooksByAuthorUseCase(authorName, excludeVolumeId = "")
            if (result is Resource.Success) {
                _uiState.value = _uiState.value.copy(books = result.data)
            }
        }
    }
}
