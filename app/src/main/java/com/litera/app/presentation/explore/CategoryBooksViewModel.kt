package com.litera.app.presentation.explore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.litera.app.core.common.Resource
import com.litera.app.core.navigation.Screen
import com.litera.app.domain.model.Book
import com.litera.app.domain.usecase.GetBooksByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryBooksUiState(
    val category: String = "",
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class CategoryBooksViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBooksByCategoryUseCase: GetBooksByCategoryUseCase
) : ViewModel() {

    private val category: String = Screen.CategoryBooks.decodeCategory(
        checkNotNull(savedStateHandle[Screen.CategoryBooks.ARG_CATEGORY])
    )

    private val _uiState = MutableStateFlow(CategoryBooksUiState(category = category))
    val uiState: StateFlow<CategoryBooksUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = getBooksByCategoryUseCase(category)) {
                is Resource.Success -> _uiState.value = _uiState.value.copy(isLoading = false, books = result.data)
                is Resource.Error -> _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                Resource.Loading -> Unit
            }
        }
    }
}
